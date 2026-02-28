package ru.yandex.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Service
public class HubEventService {
    @Value("${kafka.topic.hubs:telemetry.hubs.v1}")
    private String hubsTopic;

    private final KafkaClient kafkaClient;

    public HubEventService(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void collectHubEvent(HubEvent event) {
        HubEventAvro hubEventAvro = convertToAvro(event);
        Long timestamp = Instant.now().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
            hubsTopic,
            null,
                timestamp,
                event.getHubId(),
                hubEventAvro
        );
        kafkaClient.getProducer().send(record);
    }

    private HubEventAvro convertToAvro(HubEvent event) {
        Instant timestamp = event.getTimestamp() != null 
            ? event.getTimestamp() 
            : Instant.now();

        Object payload = createPayload(event);

        return HubEventAvro.newBuilder()
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(HubEvent event) {
        return event.extractPayload();
    }
}
