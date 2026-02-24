package ru.yandex.practicum.telemetry.collector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@RequestBody SensorEvent event) {
        eventService.collectSensorEvent(event);
        return ResponseEntity.ok().build();
    }
}
