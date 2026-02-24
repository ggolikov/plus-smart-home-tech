package ru.yandex.practicum.telemetry.collector.model;

public class MotionSensorEvent extends BaseSensorEvent {
    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }
}
