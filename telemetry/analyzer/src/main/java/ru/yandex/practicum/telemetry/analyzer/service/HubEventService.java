package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;

@Slf4j
@Service
public class HubEventService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public HubEventService(SensorRepository sensorRepository,
                           ScenarioRepository scenarioRepository,
                           ConditionRepository conditionRepository,
                           ActionRepository actionRepository) {
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
    }
    /**
     * Обработка одного события хаба:
     * - DEVICE_ADDED: сохраняем устройство (Sensor) в БД
     * - DEVICE_REMOVED: удаляем устройство из БД
     * - SCENARIO_ADDED: сохраняем сценарий в БД
     * - SCENARIO_REMOVED: удаляем сценарий из БД
     */
    public void processEvent(HubEventAvro event) {
        String hubId = event.getHubId();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(hubId, deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);
        } else {
            log.warn("Получен HubEventAvro с неизвестным типом payload: {}", payload);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        String sensorId = event.getId();
        log.info("DEVICE_ADDED: hubId={}, sensorId={}", hubId, sensorId);

        Sensor sensor = sensorRepository
                .findByIdAndHubId(sensorId, hubId)
                .orElseGet(() -> {
                    Sensor s = new Sensor();
                    s.setId(sensorId);
                    s.setHubId(hubId);
                    return s;
                });

        sensorRepository.save(sensor);
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        String sensorId = event.getId();
        log.info("DEVICE_REMOVED: hubId={}, sensorId={}", hubId, sensorId);

        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .ifPresent(sensorRepository::delete);
    }

    @Transactional
    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        String name = event.getName();
        log.info("SCENARIO_ADDED: hubId={}, name={}", hubId, name);

        // 1) Создаём/находим сценарий
        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, name)
                .orElseGet(() -> {
                    Scenario s = new Scenario();
                    s.setHubId(hubId);
                    s.setName(name);
                    s.setScenarioConditions(new ArrayList<>());
                    s.setScenarioActions(new ArrayList<>());
                    return s;
                });

        // Сначала сохраняем (особенно важно для нового сценария), чтобы получить id
        scenario = scenarioRepository.save(scenario);

        // 2) Обновляем условия/действия сценария (перезаписываем содержимое)
        if (scenario.getScenarioConditions() != null) {
            scenario.getScenarioConditions().clear();
        } else {
            scenario.setScenarioConditions(new ArrayList<>());
        }

        if (scenario.getScenarioActions() != null) {
            scenario.getScenarioActions().clear();
        } else {
            scenario.setScenarioActions(new ArrayList<>());
        }

        // 2.1) Условия
        if (event.getConditions() != null) {
            for (ScenarioConditionAvro conditionAvro : event.getConditions()) {
                if (conditionAvro == null) {
                    continue;
                }

                String sensorId = conditionAvro.getSensorId();
                Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                        .orElseGet(() -> sensorRepository.save(Sensor.builder()
                                .id(sensorId)
                                .hubId(hubId)
                                .build()));

                String type = conditionAvro.getType() != null ? conditionAvro.getType().name() : null;
                String operation = conditionAvro.getOperation() != null ? conditionAvro.getOperation().name() : null;
                Integer value = toIntegerValue(conditionAvro.getValue());

                Condition condition = conditionRepository
                        .findByTypeAndOperationAndValue(type, operation, value)
                        .orElseGet(() -> conditionRepository.save(Condition.builder()
                                .type(type)
                                .operation(operation)
                                .value(value)
                                .build()));

                ScenarioCondition link = new ScenarioCondition();
                link.setScenario(scenario);
                link.setSensor(sensor);
                link.setCondition(condition);
                link.setId(new ScenarioCondition.ScenarioConditionId(
                        scenario.getId(),
                        sensor.getId(),
                        condition.getId()
                ));

                scenario.getScenarioConditions().add(link);
            }
        }

        // 2.2) Действия
        if (event.getActions() != null) {
            for (DeviceActionAvro actionAvro : event.getActions()) {
                if (actionAvro == null) {
                    continue;
                }

                String sensorId = actionAvro.getSensorId();
                Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                        .orElseGet(() -> sensorRepository.save(Sensor.builder()
                                .id(sensorId)
                                .hubId(hubId)
                                .build()));

                String type = actionAvro.getType() != null ? actionAvro.getType().name() : null;
                Integer value = actionAvro.getValue();

                Action action = actionRepository
                        .findByTypeAndValue(type, value)
                        .orElseGet(() -> actionRepository.save(Action.builder()
                                .type(type)
                                .value(value)
                                .build()));

                ScenarioAction link = new ScenarioAction();
                link.setScenario(scenario);
                link.setSensor(sensor);
                link.setAction(action);
                link.setId(new ScenarioAction.ScenarioActionId(
                        scenario.getId(),
                        sensor.getId(),
                        action.getId()
                ));

                scenario.getScenarioActions().add(link);
            }
        }

        // 3) Сохраняем сценарий с обновлёнными связями (каскад сохранит ScenarioCondition/ScenarioAction)
        scenarioRepository.save(scenario);
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        String name = event.getName();
        log.info("SCENARIO_REMOVED: hubId={}, name={}", hubId, name);

        scenarioRepository.findByHubIdAndName(hubId, name)
                .ifPresent(scenarioRepository::delete);
    }

    private Integer toIntegerValue(Object avroUnionValue) {
        if (avroUnionValue == null) {
            return null;
        }
        if (avroUnionValue instanceof Integer i) {
            return i;
        }
        if (avroUnionValue instanceof Boolean b) {
            return b ? 1 : 0;
        }
        // на всякий случай: если прилетит другой тип, лучше не падать
        return null;
    }
}
