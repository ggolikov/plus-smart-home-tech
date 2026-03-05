package ru.yandex.practicum.telemetry.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class HubEventService {
    @Value("${kafka.topic.hubs:telemetry.hubs.v1}")
    private String hubsTopic;

    private final KafkaClient kafkaClient;

    public HubEventService(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void collectHubEvent(HubEventProto event, StreamObserver<Empty> responseObserver) {
        try {
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

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private HubEventAvro convertToAvro(HubEventProto event) {
        Instant timestamp = event.getTimestamp() != null
                ? Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos())
                : Instant.now();

        Object payload = createPayload(event);

        return HubEventAvro.newBuilder()
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(HubEventProto event) {
        HubEventProto.PayloadCase payloadCase = event.getPayloadCase();
        switch (payloadCase) {
            case DEVICE_ADDED -> {
                DeviceAddedEventProto payload = event.getDeviceAdded();
                String _type =  payload.getType().getDescriptorForType().getFullName();

                DeviceAddedEventAvro avro = new DeviceAddedEventAvro();

                avro.setId(payload.getId());
                avro.setType(DeviceTypeAvro.valueOf(_type));

                return avro;
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEventProto payload = event.getDeviceRemoved();
                DeviceRemovedEventAvro avro = new DeviceRemovedEventAvro();

                avro.setId(payload.getId());

                return avro;
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEventProto payload = event.getScenarioAdded();
                ScenarioAddedEventAvro avro = new ScenarioAddedEventAvro();

                avro.setName(payload.getName());
                avro.setActions(new ArrayList<>());
                avro.setConditions(new ArrayList<>());
//                avro.setActions(payload.getActionList());
//                avro.setConditions(payload.getConditionList());
                return avro;
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEventProto payload = event.getScenarioRemoved();
                ScenarioRemovedEventAvro avro = new ScenarioRemovedEventAvro();
                avro.setName(payload.getName());

                return avro;
            }
            default -> throw new IllegalStateException("Unexpected value: " + payloadCase);
        }
    }
}
