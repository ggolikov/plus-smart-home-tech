package ru.yandex.practicum.telemetry.collector.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Instant;
import java.util.Properties;

@Service
public class EventService {

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    private KafkaProducer<String, SensorEventAvro> producer;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        producer = new KafkaProducer<>(props);
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    public void collectSensorEvent(BaseSensorEvent event) {
        SensorEventAvro sensorEventAvro = convertToAvro(event);
        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(
            sensorsTopic,
            event.getId(),
            sensorEventAvro
        );
        producer.send(record);
    }

    private SensorEventAvro convertToAvro(BaseSensorEvent event) {
        long timestamp = event.getTimestamp() != null 
            ? event.getTimestamp().toEpochMilli() 
            : Instant.now().toEpochMilli();

        Object payload = createPayload(event);

        return SensorEventAvro.newBuilder()
            .setId(event.getId())
            .setHubId(event.getHubId())
            .setTimestamp(timestamp)
            .setPayload(payload)
            .build();
    }

    private Object createPayload(BaseSensorEvent event) {
        if (event instanceof ClimateSensorEvent) {
            ClimateSensorEvent climateEvent = (ClimateSensorEvent) event;
            return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateEvent.getTemperatureC() != null ? climateEvent.getTemperatureC() : 0)
                .setHumidity(climateEvent.getHumidity() != null ? climateEvent.getHumidity() : 0)
                .setCo2Level(climateEvent.getCo2Level() != null ? climateEvent.getCo2Level() : 0)
                .build();
        } else if (event instanceof LightSensorEvent) {
            LightSensorEvent lightEvent = (LightSensorEvent) event;
            return LightSensorAvro.newBuilder()
                .setLinkQuality(lightEvent.getLinkQuality() != null ? lightEvent.getLinkQuality() : 0)
                .setLuminosity(lightEvent.getLuminosity() != null ? lightEvent.getLuminosity() : 0)
                .build();
        } else if (event instanceof MotionSensorEvent) {
            MotionSensorEvent motionEvent = (MotionSensorEvent) event;
            return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionEvent.getLinkQuality() != null ? motionEvent.getLinkQuality() : 0)
                .setMotion(motionEvent.getMotion() != null ? motionEvent.getMotion() : false)
                .setVoltage(motionEvent.getVoltage() != null ? motionEvent.getVoltage() : 0)
                .build();
        } else if (event instanceof SwitchSensorEvent) {
            SwitchSensorEvent switchEvent = (SwitchSensorEvent) event;
            return SwitchSensorAvro.newBuilder()
                .setState(switchEvent.getState() != null ? switchEvent.getState() : false)
                .build();
        } else if (event instanceof TemperatureSensorEvent) {
            TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
            long tempTimestamp = tempEvent.getTimestamp() != null 
                ? tempEvent.getTimestamp().toEpochMilli() 
                : Instant.now().toEpochMilli();
            return TemperatureSensorAvro.newBuilder()
                .setId(tempEvent.getId())
                .setHubId(tempEvent.getHubId())
                .setTimestamp(tempTimestamp)
                .setTemperatureC(tempEvent.getTemperatureC() != null ? tempEvent.getTemperatureC() : 0)
                .setTemperatureF(tempEvent.getTemperatureF() != null ? tempEvent.getTemperatureF() : 0)
                .build();
        } else {
            throw new IllegalArgumentException("Unknown sensor event type: " + event.getClass().getName());
        }
    }
}
