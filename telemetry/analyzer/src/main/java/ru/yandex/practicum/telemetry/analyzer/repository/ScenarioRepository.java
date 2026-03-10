package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    /**
     * Получить все сценарии по hubId вместе со связанными conditions/actions
     * одним JPQL‑запросом (join fetch).
     */
//    @Query("""
//           SELECT DISTINCT s
//           FROM Scenario s
//           LEFT JOIN FETCH s.scenarioConditions sc
//           LEFT JOIN FETCH sc.sensor
//           LEFT JOIN FETCH sc.condition
//           LEFT JOIN FETCH s.scenarioActions sa
//           LEFT JOIN FETCH sa.sensor
//           LEFT JOIN FETCH sa.action
//           WHERE s.hubId = :hubId
//           """)
//    List<Scenario> findAllWithDetailsByHubId(@Param("hubId") String hubId);

    /**
     * Найти один сценарий по hubId и имени с подгруженными conditions/actions.
     */
    @Query("""
           SELECT DISTINCT s
           FROM Scenario s
           LEFT JOIN FETCH s.scenarioConditions sc
           LEFT JOIN FETCH sc.sensor
           LEFT JOIN FETCH sc.condition
           LEFT JOIN FETCH s.scenarioActions sa
           LEFT JOIN FETCH sa.sensor
           LEFT JOIN FETCH sa.action
           WHERE s.hubId = :hubId
           """)
    List<Scenario> findByHubId(String hubId);
}