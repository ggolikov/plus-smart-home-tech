package ru.yandex.practicum.telemetry.analyzer.client;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface KafkaClient {
    Consumer<String, HubEventAvro> getHubEventsConsumer();
    Consumer<String, SensorsSnapshotAvro> getSnapshotEventsConsumer();
}

