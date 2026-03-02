package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Getter
@Setter
public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorEventType getType() { return SensorEventType.LIGHT_SENSOR_EVENT; }

    @Override
    public Object extractPayload() {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(getLinkQuality() != null ? getLinkQuality() : 0)
                .setLuminosity(getLuminosity() != null ? getLuminosity() : 0)
                .build();
    }
}
