package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureSensorEvent extends SensorEvent {
    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public SensorEventType getType() { return SensorEventType.TEMPERATURE_SENSOR_EVENT; }
}
