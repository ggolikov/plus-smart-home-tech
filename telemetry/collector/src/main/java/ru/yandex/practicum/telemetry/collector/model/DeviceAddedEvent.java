package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() { return HubEventType.DEVICE_ADDED; }
}
