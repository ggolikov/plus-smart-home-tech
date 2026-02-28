package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private List<ScenarioCondition> conditions;
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() { return HubEventType.SCENARIO_ADDED; }

    @Override
    public Object extractPayload() {
            List<ScenarioConditionAvro> conditions = getConditions().stream()
                    .map(this::convertScenarioCondition)
                    .collect(Collectors.toList());
            List<DeviceActionAvro> actions = getActions().stream()
                    .map(this::convertDeviceAction)
                    .collect(Collectors.toList());
            return ScenarioAddedEventAvro.newBuilder()
                    .setName(getName())
                    .setConditions(conditions)
                    .setActions(actions)
                    .build();
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
