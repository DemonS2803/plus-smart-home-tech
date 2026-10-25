package ru.yandex.practicum.aggregator.broker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

    private final KafkaProducerProperties externalProperties;

    @Bean
    public KafkaProducer<String, SensorsSnapshotAvro> sensorSnapshotProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, externalProperties.getBootstrapAddress());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, externalProperties.getKeySerializer());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, externalProperties.getValueSerializer());

        return new KafkaProducer<>(properties);
    }

    public String getSnapshotsTopicName() {
        return externalProperties.getSnapshotTopic();
    }
}

@Data
@ConfigurationProperties("aggregator.kafka.producer")
class KafkaProducerProperties {

    private String bootstrapAddress;
    private String keySerializer;
    private String valueSerializer;
    private String snapshotTopic;

}
