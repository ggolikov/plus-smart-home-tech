package ru.yandex.practicum.telemetry.collector.model;

public class ClimateSensorEvent extends BaseSensorEvent {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getCo2Level() {
        return co2Level;
    }

    public void setCo2Level(Integer co2Level) {
        this.co2Level = co2Level;
    }
}
