package ru.yandex.practicum.analyzer.broker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {
        HubEventConsumerProperties.class,
        SnapshotConsumerProperties.class
})
public class KafkaConfiguration {

    private final HubEventConsumerProperties hubEventProperties;
    private final SnapshotConsumerProperties snapshotProperties;

    private Properties snapshotConsumerConfig() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, snapshotProperties.getBootstrapAddress());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotProperties.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotProperties.getGroupId());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotProperties.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotProperties.getValueDeserializer());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapshotProperties.isEnableAutoCommit());
        return config;
    }

    private Properties hubEventConsumerConfig() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hubEventProperties.getBootstrapAddress());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, hubEventProperties.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubEventProperties.getGroupId());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubEventProperties.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventProperties.getValueDeserializer());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hubEventProperties.isEnableAutoCommit());
        return config;
    }


    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(snapshotConsumerConfig());
        consumer.subscribe(snapshotProperties.getTopics());
        return consumer;
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(hubEventConsumerConfig());
        consumer.subscribe(hubEventProperties.getTopics());
        return consumer;
    }

}

@Data
@ConfigurationProperties(prefix = "analyzer.kafka.hub-event-consumer")
class HubEventConsumerProperties {
    private String bootstrapAddress;
    private String clientId;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private boolean enableAutoCommit = false;
    private List<String> topics = new ArrayList<>();
}

@Data
@ConfigurationProperties(prefix = "analyzer.kafka.snapshot-consumer")
class SnapshotConsumerProperties {
    private String bootstrapAddress;
    private String clientId;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private boolean enableAutoCommit = false;
    private List<String> topics = new ArrayList<>();
}