package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Getter
@Setter
public class MotionSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    @Override
    public SensorEventType getType() { return SensorEventType.MOTION_SENSOR_EVENT; }

    @Override
    public Object extractPayload() {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(getLinkQuality() != null ? getLinkQuality() : 0)
                .setMotion(getMotion() != null ? getMotion() : false)
                .setVoltage(getVoltage() != null ? getVoltage() : 0)
                .build();
    }
}
