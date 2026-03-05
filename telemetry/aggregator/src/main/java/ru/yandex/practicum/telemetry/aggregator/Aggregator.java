package ru.yandex.practicum.telemetry.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.client.KafkaClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Aggregator {
    @Value("${aggregator.kafka.producer.kafka.topic.snapshots}")
    private String snapshotsTopic;

    private final KafkaClient kafkaClient;
    private Map<String, SensorStateAvro> snapshots = new HashMap<>();

    public Aggregator(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

//    private SensorsSnapshotAvro convertToAvro(SensorEventProto event) {
//        Instant timestamp = event.getTimestamp() != null
//                ? Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos())
//                : Instant.now();
//
////        Object payload = createPayload(event);
//
//        return SensorEventAvro.newBuilder()
//                .setId(event.getId())
//                .setHubId(event.getHubId())
//                .setTimestamp(timestamp)
//                .setPayload(payload)
//                .build();
//    }

    public void handleEvent(SpecificRecordBase record) {
        log.debug("Received event {}", record);
    }

}
