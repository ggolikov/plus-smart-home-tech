package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.service.SnapshotEventService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class SnapshotProcessor {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final SnapshotEventService snapshotEventService;
    private final KafkaClient kafkaClient;
    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public SnapshotProcessor(KafkaConsumerProperties kafkaConsumerProperties, SnapshotEventService snapshotEventService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaClient = new KafkaClientImplementation();
        this.snapshotEventService = snapshotEventService;
        this.consumer = kafkaClient.getSnapshotEventsConsumer();
        consumer.subscribe(List.of(kafkaConsumerProperties.getSnapshotEventsTopic()));
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    public void start() {
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(kafkaConsumerProperties.getRequestTimeout());
                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        SensorsSnapshotAvro snapshotAvro = record.value();
                        log.info("Snapshot event received: {}", record.value());

                        snapshotEventService.processEvent(snapshotAvro);
                        manageOffsets(record, count++);
                    }
                    consumer.commitAsync();
                }
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                kafkaClient.getHubEventsConsumer().commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                kafkaClient.getHubEventsConsumer().close();
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