package ru.yandex.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
            hubsTopic,
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
        if (event instanceof DeviceAddedEvent) {
            DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
            return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(convertDeviceType(deviceAddedEvent.getDeviceType()))
                .build();
        } else if (event instanceof DeviceRemovedEvent) {
            DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
            return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
        } else if (event instanceof ScenarioAddedEvent) {
            ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
            List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditions().stream()
                .map(this::convertScenarioCondition)
                .collect(Collectors.toList());
            List<DeviceActionAvro> actions = scenarioAddedEvent.getActions().stream()
                .map(this::convertDeviceAction)
                .collect(Collectors.toList());
            return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
        } else if (event instanceof ScenarioRemovedEvent) {
            ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
            return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
        } else {
            throw new IllegalArgumentException("Unknown hub event type: " + event.getClass().getName());
        }
    }

    private DeviceTypeAvro convertDeviceType(DeviceType deviceType) {
        if (deviceType == null) {
            return null;
        }
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    private ScenarioConditionAvro convertScenarioCondition(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
            .setSensorId(condition.getSensorId())
            .setType(convertConditionType(condition.getType()))
            .setOperation(convertConditionOperation(condition.getOperation()))
            .setValue(condition.getValue())
            .build();
    }

    private ConditionTypeAvro convertConditionType(ConditionType conditionType) {
        if (conditionType == null) {
            return null;
        }
        return ConditionTypeAvro.valueOf(conditionType.name());
    }

    private ConditionOperationAvro convertConditionOperation(ConditionOperation operation) {
        if (operation == null) {
            return null;
        }
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private DeviceActionAvro convertDeviceAction(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
            .setSensorId(action.getSensorId())
            .setType(convertActionType(action.getType()))
            .setValue(action.getValue())
            .build();
    }

    private ActionTypeAvro convertActionType(ActionType actionType) {
        if (actionType == null) {
            return null;
        }
        return ActionTypeAvro.valueOf(actionType.name());
    }
}
