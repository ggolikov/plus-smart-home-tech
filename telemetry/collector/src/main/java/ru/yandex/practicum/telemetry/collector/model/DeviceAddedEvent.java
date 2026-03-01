package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Getter
@Setter
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() { return HubEventType.DEVICE_ADDED; }

    @Override
    public Object extractPayload() {
            return DeviceAddedEventAvro.newBuilder()
                    .setId(getId())
                    .setType(convertDeviceType())
                    .build();
    }

    private DeviceTypeAvro convertDeviceType() {
        if (deviceType == null) {
            return null;
        }
        return DeviceTypeAvro.valueOf(deviceType.name());
    }
}
