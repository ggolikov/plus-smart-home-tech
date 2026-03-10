package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.service.SnapshotEventService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class SnapshotProcessor implements Runnable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final SnapshotEventService snapshotEventService;
    private KafkaClient kafkaClient;
    private Consumer<String, SensorsSnapshotAvro> consumer;

    public SnapshotProcessor(KafkaConsumerProperties kafkaConsumerProperties, SnapshotEventService snapshotEventService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaClient = new KafkaClientImplementation();
        this.snapshotEventService = snapshotEventService;
    }

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getSnapshotEventsConsumer();
            consumer.subscribe(List.of(kafkaConsumerProperties.getSnapshotEventsTopic()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshotAvro = record.value();
                    log.info("Snapshot event received: {}", record.value());
                    snapshotEventService.processEvent(snapshotAvro);
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

    public void start() {
        run();
    }
}