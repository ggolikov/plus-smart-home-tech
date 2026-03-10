package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.model.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SnapshotEventService {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final ScenarioRepository scenarioRepository;

    public SnapshotEventService(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
            ScenarioRepository scenarioRepository
    ) {
        this.hubRouterClient = hubRouterClient;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Алгоритм:
     * 1. По hubId снапшота загружаем все сценарии из БД.
     * 2. Для каждого сценария проверяем, выполняются ли ВСЕ его условия.
     * 3. Если условия выполняются, для каждого действия сценария отправляем gRPC-запрос в хаб.
     */
    @Transactional(readOnly = true)
    public void processEvent(SensorsSnapshotAvro snapshot) {
        if (snapshot == null) {
            return;
        }

        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        if (sensorsState == null || sensorsState.isEmpty()) {
            log.debug("Пустой снапшот для hubId={}, действий нет", hubId);
            return;
        }

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.debug("Для hubId={} нет сценариев, действий нет", hubId);
            return;
        }

        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, sensorsState)) {
                log.info("Сценарий '{}' для hubId={} сработал", scenario.getName(), hubId);
                executeScenarioActions(hubId, scenario);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, Map<String, SensorStateAvro> sensorsState) {
        Map<String, Condition> conditions = scenario.getScenarioConditions();
        if (conditions == null || conditions.isEmpty()) {
            // Нет условий — трактуем как "всегда истинный" сценарий
            return true;
        }

        for (ScenarioCondition scenarioCondition : conditions.get(sensorsState.)) {
            SensorStateAvro sensorState = sensorsState.get(
                    scenarioCondition.getSensor().getId()
            );

            if (sensorState == null) {
                // Нет данных по нужному датчику — условие ложно
                return false;
            }

            Condition condition = scenarioCondition.getCondition();
            if (condition == null) {
                // Невалидное состояние БД — лучше считать условие ложным
                return false;
            }

            Integer sensorValue = extractSensorValue(sensorState, condition.getType());
            if (sensorValue == null) {
                // Не удалось получить значение датчика по типу — условие ложно
                return false;
            }

            if (!checkCondition(sensorValue, condition.getOperation(), condition.getValue())) {
                // Как только одно условие ложно — сценарий не активируется
                return false;
            }
        }

        // Все условия истинны
        return true;
    }

    /**
     * Извлекает числовое значение из состояния датчика по типу условия.
     * Ожидается, что поле Condition.type содержит один из значений:
     * MOTION, LUMINOSITY, SWITCH, TEMPERATURE, CO2LEVEL, HUMIDITY.
     */
    private Integer extractSensorValue(SensorStateAvro sensorState, String type) {
        Object payload = sensorState.getData();
        if (payload == null || type == null) {
            return null;
        }

        // Для простоты: сопоставляем тип условия с конкретным Avro-классом и полем
        return switch (type) {
            case "MOTION" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro motion) {
                    yield motion.getMotion() ? 1 : 0;
                }
                yield null;
            }
            case "LUMINOSITY" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro light) {
                    yield light.getLuminosity();
                }
                yield null;
            }
            case "SWITCH" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro sw) {
                    yield sw.getState() ? 1 : 0;
                }
                yield null;
            }
            case "TEMPERATURE" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro temp) {
                    yield temp.getTemperatureC();
                }
                yield null;
            }
            case "CO2LEVEL" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro climate) {
                    yield climate.getCo2Level();
                }
                yield null;
            }
            case "HUMIDITY" -> {
                if (payload instanceof ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro climate) {
                    yield climate.getHumidity();
                }
                yield null;
            }
            default -> null;
        };
    }

    /**
     * Проверка условия: БОЛЬШЕ, МЕНЬШЕ, РАВНО.
     * В БД в поле operation ожидаем строки: GREATER_THAN, LOWER_THAN, EQUALS.
     */
    private boolean checkCondition(int sensorValue, String operation, Integer expected) {
        if (operation == null || expected == null) {
            return false;
        }

        return switch (operation) {
            case "GREATER_THAN" -> sensorValue > expected;
            case "LOWER_THAN" -> sensorValue < expected;
            case "EQUALS" -> sensorValue == expected;
            default -> false;
        };
    }

    private void executeScenarioActions(String hubId, Scenario scenario) {
        Map<String, Action> actions = scenario.getScenarioActions();
        if (actions == null || actions.isEmpty()) {
            return;
        }

//        for (ScenarioAction scenarioAction : actions) {
//            DeviceActionProto actionProto = buildDeviceActionProto(scenarioAction);
//            if (actionProto == null) {
//                continue;
//            }
//
//            DeviceActionRequest request = DeviceActionRequest.newBuilder()
//                    .setHubId(hubId)
//                    .setScenarioName(scenario.getName())
//                    .setAction(actionProto)
//                    .setTimestamp(toProtoTimestamp(Instant.now()))
//                    .build();
//
//            log.info("Отправляем действие в HubRouter: {}", request);
//            hubRouterClient.handleDeviceAction(request);
//        }
    }

    private DeviceActionProto buildDeviceActionProto(ScenarioAction scenarioAction) {
        if (scenarioAction.getSensor() == null || scenarioAction.getAction() == null) {
            return null;
        }

        String sensorId = scenarioAction.getSensor().getId();
        String type = scenarioAction.getAction().getType();
        Integer value = scenarioAction.getAction().getValue();

        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId);

        try {
            ActionTypeProto actionTypeProto =
                    ActionTypeProto.valueOf(type);
            builder.setType(actionTypeProto);
        } catch (IllegalArgumentException e) {
            log.warn("Неизвестный тип действия '{}', пропускаем", type);
            return null;
        }

        if (value != null) {
            builder.setValue(value);
        }

        return builder.build();
    }

    private com.google.protobuf.Timestamp toProtoTimestamp(Instant instant) {
        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
