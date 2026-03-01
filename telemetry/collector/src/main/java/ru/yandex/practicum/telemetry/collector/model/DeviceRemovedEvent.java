package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Getter
@Setter
public class DeviceRemovedEvent extends HubEvent {
    private String id;

    @Override
    public HubEventType getType() { return HubEventType.DEVICE_REMOVED; }

    @Override
    public Object extractPayload() {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(getId())
                .build();

    }
}
