package ru.yandex.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.*;

import java.time.Instant;

@Service
public class SensorEventService {
    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    private final KafkaClient kafkaClient;


    public SensorEventService(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void collectSensorEvent(SensorEvent event) {
        SensorEventAvro sensorEventAvro = convertToAvro(event);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
            sensorsTopic,
            event.getId(),
            sensorEventAvro
        );
        kafkaClient.getProducer().send(record);
    }

    private SensorEventAvro convertToAvro(SensorEvent event) {
        Instant timestamp = event.getTimestamp() != null
            ? event.getTimestamp()
            : Instant.now();

        Object payload = createPayload(event);

        return SensorEventAvro.newBuilder()
            .setId(event.getId())
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(SensorEvent event) {
        return event.extractPayload();
    }
}
