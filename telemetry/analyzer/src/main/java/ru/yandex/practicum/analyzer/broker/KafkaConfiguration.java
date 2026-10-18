package ru.yandex.practicum.analyzer.broker;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.common.utils.KafkaConstants;
import ru.yandex.practicum.telemetry.serialization.avroschemas.deserialize.HubEventAvroDeserializer;
import ru.yandex.practicum.telemetry.serialization.avroschemas.deserialize.SensorsSnapshotAvroDeserializer;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private Properties snapshotConsumerConfig() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ANALYZER_SNAPSHOTS_CONSUMER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.CONSUMER_GROUP_SNAPSHOTS);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotAvroDeserializer.class.getName());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return config;
    }

    private Properties hubEventConsumerConfig() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ANALYZER_HUB_CONSUMER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.CONSUMER_GROUP_HUB_EVENTS);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventAvroDeserializer.class.getName());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return config;
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        KafkaConsumer<String, SensorsSnapshotAvro> consumer = new KafkaConsumer<>(snapshotConsumerConfig());
        consumer.subscribe(java.util.Collections.singletonList(KafkaConstants.TOPIC_TELEMETRY_SNAPSHOTS_V1));
        return consumer;
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(hubEventConsumerConfig());
        consumer.subscribe(java.util.Collections.singletonList(KafkaConstants.TOPIC_TELEMETRY_HUBS_V1));
        return consumer;
    }

}
