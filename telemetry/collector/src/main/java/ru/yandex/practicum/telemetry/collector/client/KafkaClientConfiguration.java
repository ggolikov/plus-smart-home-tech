package ru.yandex.practicum.telemetry.collector.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KafkaClientConfiguration {
    @Bean
    @Scope("prototype")
    public static KafkaClient initClient() {
        return new KafkaClientImplementation();
    }

    private final KafkaClient kafkaClient;

    public KafkaClientConfiguration() {
        if (this.getClient() == null) {
            this.kafkaClient = initClient();
        } else {
            this.kafkaClient = this.getClient();
        }
    }

    KafkaClient getClient() {
        return kafkaClient;
    }
}
