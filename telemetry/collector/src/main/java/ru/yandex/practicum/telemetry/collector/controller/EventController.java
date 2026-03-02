package ru.yandex.practicum.telemetry.collector.controller;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase  {
    private final SensorEventService sensorEventService;
    private final HubEventService hubEventService;

    public EventController(SensorEventService sensorEventService, HubEventService hubEventService) {
        this.sensorEventService = sensorEventService;
        this.hubEventService = hubEventService;
    }

    public void collectSensorEvent(SensorEventProto event, StreamObserver<Empty> responseObserver) {
        sensorEventService.collectSensorEvent(event, responseObserver);
    }

    public void collectHubEvent(HubEventProto event, StreamObserver<Empty> responseObserver) {
        hubEventService.collectHubEvent(event, responseObserver);
    }
}
