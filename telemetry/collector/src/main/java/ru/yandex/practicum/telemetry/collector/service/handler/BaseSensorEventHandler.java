package ru.yandex.practicum.telemetry.collector.service.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    Producer<String, String> producer;

    @Override
    public void handle(HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalStateException("Unexpected event type: " + event.getType());
        }

//        T payload = mapToAvro(event);

//        HubEventAvro eventAvro = HubEventAvro.newBuilder().setHubId(event.getHubId()).setTimestamp(event.getTimestamp())/*.setPayload(payload)*/.build();

//        producer.send(eventAvro, event.getHubId(), event.getTimestamp());
    }
}
