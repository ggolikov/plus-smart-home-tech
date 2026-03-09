package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClient;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaClientImplementation;
import ru.yandex.practicum.telemetry.analyzer.client.KafkaConsumerProperties;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class HubEventProcessor implements Runnable {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private KafkaClient kafkaClient;

    public HubEventProcessor(KafkaConsumerProperties kafkaConsumerProperties,
                             SensorRepository sensorRepository,
                             ScenarioRepository scenarioRepository) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
        this.kafkaClient = new KafkaClientImplementation();
    }

    @Override
    public void run() {
        Consumer<String, HubEventAvro> consumer = null;
        try {
            consumer = kafkaClient.getHubEventsConsumer();
            consumer.subscribe(List.of(kafkaConsumerProperties.getHubEventsTopic()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    if (event == null) {
                        continue;
                    }
                    log.info("Hub event received: {}", event);
                    processHubEvent(event);
                }
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                if (consumer != null) {
                    // фиксируем смещения для обработанных сообщений
                    consumer.commitSync();
                }
            } finally {
                if (consumer != null) {
                    log.info("Закрываем консьюмер");
                    consumer.close();
                }
            }
        }
    }

    public void start() {
        run();
    }

    /**
     * Обработка одного события хаба:
     * - DEVICE_ADDED: сохраняем устройство (Sensor) в БД
     * - DEVICE_REMOVED: удаляем устройство из БД
     * - SCENARIO_ADDED: сохраняем сценарий в БД
     * - SCENARIO_REMOVED: удаляем сценарий из БД
     */
    private void processHubEvent(HubEventAvro event) {
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