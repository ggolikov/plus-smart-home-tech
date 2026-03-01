package ru.yandex.practicum.telemetry.collector.client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import java.util.Properties;

public class KafkaClientImplementation implements KafkaClient, AutoCloseable {
    @Value("${collector.kafka.producer.properties.bootstrap-servers}")
    String bootstrapServers;

    @Value("${collector.kafka.producer.properties.acks}")
    String acks;

    @Value("${collector.kafka.producer.properties.retries}")
    Integer retries;

    private Producer<String, SpecificRecordBase> producer;

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);

        producer = new KafkaProducer<>(config);
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
