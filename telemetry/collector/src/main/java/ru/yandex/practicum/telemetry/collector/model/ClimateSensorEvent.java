package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public SensorEventType getType() { return SensorEventType.CLIMATE_SENSOR_EVENT; }
}
