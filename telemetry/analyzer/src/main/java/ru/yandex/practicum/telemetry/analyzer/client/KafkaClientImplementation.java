package ru.yandex.practicum.telemetry.analyzer.client;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserializer.HubEventDeserializer;
import ru.yandex.practicum.kafka.deserializer.SnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Component
@EnableConfigurationProperties({KafkaConsumerProperties.class})
public class KafkaClientImplementation implements KafkaClient, AutoCloseable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private Consumer<String, HubEventAvro> hubEventsConsumer;
    private Consumer<String, SensorsSnapshotAvro> snapshotEventsConsumer;

    public KafkaClientImplementation(KafkaConsumerProperties kafkaConsumerProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    public KafkaClientImplementation() {
        this.kafkaConsumerProperties = new KafkaConsumerProperties();
    }

    @Override
    public Consumer<String, HubEventAvro> getHubEventsConsumer() {
        if (hubEventsConsumer == null) {
            initHubEventsConsumer();
        }

        return hubEventsConsumer;
    }

    private void initHubEventsConsumer() {
        Properties config = new Properties();
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrapServers());
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "telemetry.hubs.v1");

        hubEventsConsumer = new KafkaConsumer<>(config);
    }

    @Override
    public Consumer<String, SensorsSnapshotAvro> getSnapshotEventsConsumer() {
        if (snapshotEventsConsumer == null) {
            initSnapshotEventsConsumer();
        }

        return snapshotEventsConsumer;
    }

    private void initSnapshotEventsConsumer() {
        Properties config = new Properties();
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrapServers());
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class.getName());
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "telemetry.hubs.v1");

        snapshotEventsConsumer = new KafkaConsumer<>(config);
    }

    @Override
    public void close() {}
}
