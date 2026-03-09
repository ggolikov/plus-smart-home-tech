package ru.yandex.practicum.telemetry.analyzer.repository;

import java.util.Collection;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);
    Optional<Sensor> findByIdAndHubId(String id, String hubId);
}
