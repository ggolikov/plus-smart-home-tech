package ru.yandex.practicum.telemetry.aggregator;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;
@Slf4j
@Component
public class EventDataProducer {
    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

     SensorEventProto createTemperatureSensorEvent() {
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId("1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                )
                .build();
    }

    private void sendEvent() {
        SensorEventProto event = createTemperatureSensorEvent();
        log.info("Отправляю данные: {}", event.getAllFields());
        Object response = collectorStub.collectSensorEvent(event);
        log.info("Получил ответ от коллектора: {}", response);
    }

}