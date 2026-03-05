package ru.yandex.practicum.telemetry.aggregator.client;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface KafkaClient {
    Producer<String, SensorsSnapshotAvro> getProducer();
    Consumer<String, SensorEventAvro> getConsumer();
}

