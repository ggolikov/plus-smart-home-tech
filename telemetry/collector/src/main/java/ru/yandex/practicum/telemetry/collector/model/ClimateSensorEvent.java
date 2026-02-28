package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public SensorEventType getType() { return SensorEventType.CLIMATE_SENSOR_EVENT; }

    @Override
    public Object extractPayload() {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(getTemperatureC() != null ? getTemperatureC() : 0)
                .setHumidity(getHumidity() != null ? getHumidity() : 0)
                .setCo2Level(getCo2Level() != null ? getCo2Level() : 0)
                .build();
    }
}
