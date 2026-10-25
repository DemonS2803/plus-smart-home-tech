package ru.yandex.practicum.telemetry.collector.broker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {
        HubEventProducerProperties.class,
        SensorEventProducerProperties.class
})
public class KafkaProducerConfiguration {
    private final HubEventProducerProperties hubEventProperties;
    private final SensorEventProducerProperties sensorEventProperties;


    @Bean
    public KafkaProducer<String, HubEventAvro> hubEventProducer() {
        return new KafkaProducer<>(hubEventConfig());
    }

    public String getHubEventTopic() {
        return hubEventProperties.getHubEventTopic();
    }

    private Properties hubEventConfig() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hubEventProperties.getBootstrapAddress());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, hubEventProperties.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, hubEventProperties.getValueSerializer());
        config.put(ProducerConfig.LINGER_MS_CONFIG, hubEventProperties.getLingerMs());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, hubEventProperties.getBatchSize());
        return config;
    }

    @Bean
    public KafkaProducer<String, SensorEventAvro> sensorEventProducer() {
        return new KafkaProducer<>(sensorEventConfig());
    }

    public String getSensorEventTopic() {
        return sensorEventProperties.getSensorEventTopic();
    }

    private Properties sensorEventConfig() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sensorEventProperties.getBootstrapAddress());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, sensorEventProperties.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, sensorEventProperties.getValueSerializer());
        config.put(ProducerConfig.LINGER_MS_CONFIG, sensorEventProperties.getLingerMs());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, sensorEventProperties.getBatchSize());
        return config;
    }

}

@Data
@ConfigurationProperties(prefix = "collector.kafka.hub-event-producer")
class HubEventProducerProperties {
    private String bootstrapAddress;
    private String keySerializer;
    private String valueSerializer;
    private int lingerMs = 0;
    private int batchSize = 1;
    private String hubEventTopic;
}

@Data
@ConfigurationProperties(prefix = "collector.kafka.sensor-event-producer")
class SensorEventProducerProperties {
    private String bootstrapAddress;
    private String keySerializer;
    private String valueSerializer;
    private int lingerMs = 1000;
    private int batchSize = 1000;
    private String sensorEventTopic;
}
