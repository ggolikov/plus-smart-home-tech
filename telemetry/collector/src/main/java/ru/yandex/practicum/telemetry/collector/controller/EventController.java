package ru.yandex.practicum.telemetry.collector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final SensorEventService sensorEventService;
    private final HubEventService hubEventService;

    public EventController(SensorEventService sensorEventService, HubEventService hubEventService) {
        this.sensorEventService = sensorEventService;
        this.hubEventService = hubEventService;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@RequestBody SensorEvent event) {
        sensorEventService.collectSensorEvent(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@RequestBody HubEvent event) {
        hubEventService.collectHubEvent(event);
        return ResponseEntity.ok().build();
    }
}
