package ru.yandex.practicum.telemetry.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;

import java.time.Instant;

@Service
public class SensorEventService {
    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    private final KafkaClient kafkaClient;


    public SensorEventService(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void collectSensorEvent(SensorEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventAvro sensorEventAvro = convertToAvro(event);
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                    sensorsTopic,
                    event.getId(),
                    sensorEventAvro
            );
            kafkaClient.getProducer().send(record);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private SensorEventAvro convertToAvro(SensorEventProto event) {
        Instant timestamp = event.getTimestamp() != null
            ? Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos())
            : Instant.now();

        Object payload = createPayload(event);

        return SensorEventAvro.newBuilder()
            .setId(event.getId())
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(SensorEventProto event) {
        return event.getPayloadCase();
    }
}
