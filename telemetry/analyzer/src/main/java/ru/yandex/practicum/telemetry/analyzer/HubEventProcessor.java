package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.service.HubEventService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class HubEventProcessor implements Runnable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final HubEventService hubEventService;
    private final KafkaClient kafkaClient;
    private final Consumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public HubEventProcessor(KafkaConsumerProperties kafkaConsumerProperties, KafkaClient kafkaClient,
                             HubEventService hubEventService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaClient = new KafkaClientImplementation();
        this.hubEventService = hubEventService;
        consumer = kafkaClient.getHubEventsConsumer();
        consumer.subscribe(List.of(kafkaConsumerProperties.getHubEventsTopic()));
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    @Override
    public void run() {
        try {
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(kafkaConsumerProperties.getRequestTimeout());
                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<String, HubEventAvro> record : records) {
                        HubEventAvro event = record.value();
                        if (event == null) {
                            continue;
                        }
                        log.info("Hub event received: {}", event);
                        hubEventService.processEvent(event);
                        manageOffsets(record, count++);
                    }
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                if (consumer != null) {
                    // фиксируем смещения для обработанных сообщений
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                if (consumer != null) {
                    log.info("Закрываем консьюмер");
                    consumer.close();
                }
            }
        }
    }

    private void manageOffsets(ConsumerRecord<?, ?> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 100 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}