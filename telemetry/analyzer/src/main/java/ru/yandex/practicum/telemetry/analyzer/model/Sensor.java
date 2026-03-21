package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "hub_id")
    private String hubId;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScenarioCondition> scenarioConditions = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScenarioAction> scenarioActions = new ArrayList<>();
}
