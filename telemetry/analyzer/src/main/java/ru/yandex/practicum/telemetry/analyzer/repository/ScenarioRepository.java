package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
//    /**
//     * Получить все сценарии по hubId вместе со связанными conditions/actions
//     * одним JPQL‑запросом (join fetch).
//     */
//    @Query("""
//           SELECT DISTINCT s
//           FROM Scenario s
//           LEFT JOIN FETCH s.scenarioConditions sc
//           LEFT JOIN FETCH s.scenarioActions sa
//           LEFT JOIN FETCH sa.action
//           WHERE s.hubId = :hubId
//           """)
//    Set<Scenario> findAllWithDetailsByHubId(String hubId);
//
//    /**
//     * Найти один сценарий по hubId и имени с подгруженными conditions/actions.
//     */
//    @Query("""
//           SELECT DISTINCT s
//           FROM Scenario s
//           LEFT JOIN FETCH s.scenarioConditions sc
//           LEFT JOIN FETCH s.scenarioActions sa
//           LEFT JOIN FETCH sa.action
//           WHERE s.hubId = :hubId AND s.name = :name
//           """)
//    Optional<Scenario> findByHubIdAndName(String hubId, String name);
//}

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByHubId(String hubId);
    Optional<Scenario> findByHubIdAndName(String hubId, String name);
}