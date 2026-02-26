package ru.yandex.practicum.telemetry.collector.service.handler;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.collector.model.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;

@Component(value = "DEVICE_ADDED")
public class DeviceRemovedHubEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
//    public DeviceAddedHubEventHandler(KafkaProducer producer) {
//        super(producer);
//    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent _event = (DeviceAddedEvent) event;

        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
//                .setType(EnumMapper.map(_event.getDeviceType(), DeviceTypeAvro.class))
                .setType(_event.getDeviceType())
                .build();
    }

}
