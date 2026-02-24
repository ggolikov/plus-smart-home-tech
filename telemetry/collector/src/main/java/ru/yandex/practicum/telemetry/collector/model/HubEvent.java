package ru.yandex.practicum.telemetry.collector.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)

@JsonSubTypes({
    @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
    @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
    @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
    @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Schema(description = "Абстрактный класс для представления событий хаба")
public abstract class HubEvent {
    @Schema(description = "Идентификатор хаба, связанный с событием.")
    @NotNull
    private String hubId;

    @Schema(description = "Временная метка события")
    private Instant timestamp;

    @Schema(description = "Тип события хаба")
    public abstract HubEventType getType();

}
