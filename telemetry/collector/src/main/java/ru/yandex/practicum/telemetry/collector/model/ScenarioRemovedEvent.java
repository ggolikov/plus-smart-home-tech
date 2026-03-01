package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Getter
@Setter
public class ScenarioRemovedEvent extends HubEvent {
    private String name;

    @Override
    public HubEventType getType() { return HubEventType.SCENARIO_REMOVED; }


    @Override
    public Object extractPayload() {
            return ScenarioRemovedEventAvro.newBuilder()
                    .setName(getName())
                    .build();
    }
}
