package ru.yandex.practicum.telemetry.aggregator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class Aggregator {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.debug("Received event {}", event);
        String hubId = event.getHubId();

        // 1. Найти или создать снапшот для хаба
        SensorsSnapshotAvro snapshot = snapshots.get(hubId);
        if (snapshot == null) {
            snapshot = new SensorsSnapshotAvro();
            snapshot.setHubId(hubId);
            snapshot.setTimestamp(Instant.now());

            snapshot.setSensorsState(new HashMap<>());
            snapshots.put(hubId, snapshot);
        }

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        if (sensorsState == null) {
            sensorsState = new HashMap<>();
            snapshot.setSensorsState(sensorsState);
        }

        // 2. Проверить, есть ли состояние конкретного сенсора
        SensorStateAvro oldState = sensorsState.get(event.getId());
        if (oldState != null) {
            boolean oldIsNewer = oldState.getTimestamp().isAfter(event.getTimestamp());
            boolean sameData = oldState.getData().equals(event.getPayload());

            // Если старое состояние более свежее ИЛИ данные не изменились —
            // обновлять снапшот не нужно.
            if (oldIsNewer || sameData) {
                return Optional.empty();
            }
        }

        // 3. Пришли новые данные — обновляем состояние сенсора в снапшоте
        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());
        sensorsState.put(event.getId(), newState);

        // Обновляем таймстемп снапшота
        snapshot.setTimestamp(Instant.now());

        return Optional.of(snapshot);
    }
}
