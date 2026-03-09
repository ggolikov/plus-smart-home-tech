package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class HubEventProcessor implements Runnable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private KafkaClient kafkaClient;

    public HubEventProcessor(KafkaConsumerProperties kafkaConsumerProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaClient = new KafkaClientImplementation();
    }

    @Override
    public void run() {
        try {
            Consumer<String, HubEventAvro> consumer = kafkaClient.getHubEventsConsumer();
            consumer.subscribe(List.of(kafkaConsumerProperties.getHubEventsTopic()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.info("Hub event received: {}", record.value());
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                // здесь нужно вызвать метод консьюмера для фиксации смещений
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
    // ...детали реализации...
}