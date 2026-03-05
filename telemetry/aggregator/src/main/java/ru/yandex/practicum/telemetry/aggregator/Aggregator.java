package ru.yandex.practicum.telemetry.aggregator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class Aggregator {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.debug("Received event {}", event);
        SensorsSnapshotAvro sensorsSnapshotAvro;
        String hubId = event.getHubId();

        if (snapshots.containsKey(hubId)) {
            sensorsSnapshotAvro = snapshots.get(hubId);
        } else {
            sensorsSnapshotAvro = new SensorsSnapshotAvro();
            snapshots.put(hubId, sensorsSnapshotAvro);
        }

        Map<String, SensorStateAvro> sensorsState = sensorsSnapshotAvro.getSensorsState();

        if (sensorsState.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsState.get(event.getId());

            if (oldState.getTimestamp().isBefore(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());
        sensorsState.put(event.getId(), newState);

        return Optional.of(sensorsSnapshotAvro);
    }
}
