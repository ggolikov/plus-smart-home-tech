package ru.yandex.practicum.telemetry.analyzer.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "analyzer.kafka.consumer.properties")
public class KafkaConsumerProperties {
    private String bootstrapServers;
    private String groupId;
    private String hubEventsTopic;
    private String snapshotEventsTopic;
    private Duration requestTimeout = Duration.ofMillis(100);
}
