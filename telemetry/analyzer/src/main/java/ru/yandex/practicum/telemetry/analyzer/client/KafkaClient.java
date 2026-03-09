package ru.yandex.practicum.telemetry.analyzer.client;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaClient {
    Producer<String, SpecificRecordBase> getProducer();
}

