package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@Getter
@Setter
public class TemperatureSensorEvent extends SensorEvent {
    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public SensorEventType getType() { return SensorEventType.TEMPERATURE_SENSOR_EVENT; }

    @Override
    public Object extractPayload() {
        Instant tempTimestamp = getTimestamp() != null
                ? getTimestamp()
                : Instant.now();
        return TemperatureSensorAvro.newBuilder()
                .setId(getId())
                .setHubId(getHubId())
                .setTimestamp(tempTimestamp)
                .setTemperatureC(getTemperatureC() != null ? getTemperatureC() : 0)
                .setTemperatureF(getTemperatureF() != null ? getTemperatureF() : 0)
                .build();
    }
}
