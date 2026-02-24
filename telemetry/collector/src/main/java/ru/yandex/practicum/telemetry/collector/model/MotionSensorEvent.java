package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotionSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    @Override
    public SensorEventType getType() { return SensorEventType.MOTION_SENSOR_EVENT; }
}
