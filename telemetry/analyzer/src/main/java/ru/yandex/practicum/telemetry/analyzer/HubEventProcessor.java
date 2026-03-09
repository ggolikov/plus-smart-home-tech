package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.service.HubEventService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class HubEventProcessor implements Runnable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final HubEventService hubEventService;
    private KafkaClient kafkaClient;
    private Consumer<String, HubEventAvro> consumer;

    public HubEventProcessor(KafkaConsumerProperties kafkaConsumerProperties, KafkaClient kafkaClient,
                             HubEventService hubEventService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaClient = new KafkaClientImplementation();
        this.hubEventService = hubEventService;
    }

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getHubEventsConsumer();
            consumer.subscribe(List.of(kafkaConsumerProperties.getHubEventsTopic()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    if (event == null) {
                        continue;
                    }
                    log.info("Hub event received: {}", event);
                    hubEventService.processEvent(event);
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
                    consumer.commitSync();
                }
            } finally {
                if (consumer != null) {
                    log.info("Закрываем консьюмер");
                    consumer.close();
                }
            }
        }
    }

    public void start() {
        run();
    }
}