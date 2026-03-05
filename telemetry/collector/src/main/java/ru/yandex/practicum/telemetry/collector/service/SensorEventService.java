package ru.yandex.practicum.telemetry.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.client.KafkaClient;

import java.time.Instant;

@Service
public class SensorEventService {
    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    private final KafkaClient kafkaClient;


    public SensorEventService(KafkaClient kafkaClient) {
        this.kafkaClient = kafkaClient;
    }

    public void collectSensorEvent(SensorEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventAvro sensorEventAvro = convertToAvro(event);
            Long timestamp = Instant.now().toEpochMilli();

            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                    sensorsTopic,
                    null,
                    timestamp,
                    event.getId(),
                    sensorEventAvro
            );

            kafkaClient.getProducer().send(record);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private SensorEventAvro convertToAvro(SensorEventProto event) {
        Instant timestamp = event.getTimestamp() != null
            ? Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos())
            : Instant.now();

        Object payload = createPayload(event);

        return SensorEventAvro.newBuilder()
            .setId(event.getId())
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(SensorEventProto event) {
        SensorEventProto.PayloadCase payloadCase = event.getPayloadCase();
        switch (payloadCase) {
            case LIGHT_SENSOR -> {
                LightSensorProto payload = event.getLightSensor();
                LightSensorAvro avro = new LightSensorAvro();

                avro.setLinkQuality(payload.getLinkQuality());

                return avro;
            }
            case CLIMATE_SENSOR -> {
                ClimateSensorProto payload = event.getClimateSensor();

                ClimateSensorAvro avro = new ClimateSensorAvro();

                avro.setTemperatureC(payload.getTemperatureC());
                avro.setHumidity(payload.getHumidity());
                avro.setCo2Level(payload.getCo2Level());

                return avro;
            }
            case TEMPERATURE_SENSOR -> {
                TemperatureSensorProto payload = event.getTemperatureSensor();

                TemperatureSensorAvro avro = new TemperatureSensorAvro();
                avro.setTemperatureC(payload.getTemperatureC());
                avro.setTemperatureF(payload.getTemperatureF());

                return avro;
            }
            case MOTION_SENSOR -> {
                MotionSensorProto payload = event.getMotionSensor();

                MotionSensorAvro avro = new MotionSensorAvro();
                avro.setLinkQuality(payload.getLinkQuality());
                avro.setMotion(payload.getMotion());
                avro.setVoltage(payload.getVoltage());

                return avro;
            }
            case SWITCH_SENSOR -> {
                SwitchSensorProto payload = event.getSwitchSensor();

                SwitchSensorAvro avro = new SwitchSensorAvro();

                avro.setState(payload.getState());

                return avro;
            }
            default -> throw new IllegalStateException("Unexpected value: " + payloadCase);
        }
    }
}
