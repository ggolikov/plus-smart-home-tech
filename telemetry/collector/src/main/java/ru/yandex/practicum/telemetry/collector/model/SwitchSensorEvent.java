package ru.yandex.practicum.telemetry.collector.model;

public class SwitchSensorEvent extends BaseSensorEvent {
    private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
