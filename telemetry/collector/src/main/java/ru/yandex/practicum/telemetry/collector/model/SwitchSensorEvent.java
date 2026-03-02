package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Getter
@Setter
public class SwitchSensorEvent extends SensorEvent {
    private Boolean state;

    @Override
    public SensorEventType getType() { return SensorEventType.SWITCH_SENSOR_EVENT; }

    @Override
    public Object extractPayload() {
        return SwitchSensorAvro.newBuilder()
                .setState(getState() != null ? getState() : false)
                .build();
    }
}
