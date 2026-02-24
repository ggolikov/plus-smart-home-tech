package ru.yandex.practicum.telemetry.collector.model;

public class TemperatureSensorEvent extends BaseSensorEvent {
    private Integer temperatureC;
    private Integer temperatureF;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Integer getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(Integer temperatureF) {
        this.temperatureF = temperatureF;
    }
}
