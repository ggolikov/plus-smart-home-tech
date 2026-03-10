package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
public class HubEventService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    public HubEventService(SensorRepository sensorRepository,
                           ScenarioRepository scenarioRepository) {
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
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

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        String name = event.getName();
        log.info("SCENARIO_ADDED: hubId={}, name={}", hubId, name);

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, name)
                .orElseGet(() -> {
                    Scenario s = new Scenario();
                    s.setHubId(hubId);
                    s.setName(name);
                    return s;
                });

        scenarioRepository.save(scenario);
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        String name = event.getName();
        log.info("SCENARIO_REMOVED: hubId={}, name={}", hubId, name);

        scenarioRepository.findByHubIdAndName(hubId, name)
                .ifPresent(scenarioRepository::delete);
    }
}
