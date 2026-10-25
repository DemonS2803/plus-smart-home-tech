package ru.yandex.practicum.aggregator.broker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties externalProperties;

    @Bean
    public KafkaConsumer<String, SensorEventAvro> sensorEventConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, externalProperties.getBootstrapAddress());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, externalProperties.getClientId());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, externalProperties.getGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, externalProperties.getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, externalProperties.getValueDeserializer());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, externalProperties.isEnableAutoCommit());

        KafkaConsumer<String, SensorEventAvro> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(externalProperties.getTopics());

        return consumer;
    }

}

@Data
@ConfigurationProperties("aggregator.kafka.consumer")
class KafkaConsumerProperties {
    private String bootstrapAddress;
    private String clientId;
    private String groupId;
    private String keyDeserializer;
    private String valueDeserializer;
    private boolean enableAutoCommit;
    private List<String> topics;
}
