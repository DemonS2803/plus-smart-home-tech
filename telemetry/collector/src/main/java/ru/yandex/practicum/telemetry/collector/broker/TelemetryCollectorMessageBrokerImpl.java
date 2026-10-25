package ru.yandex.practicum.telemetry.collector.broker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryCollectorMessageBrokerImpl implements TelemetryCollectorMessageBroker {

    private final KafkaProducer<String, HubEventAvro> hubEventProducer;
    private final KafkaProducer<String, SensorEventAvro> sensorEventProducer;
    private final KafkaProducerConfiguration kafkaProducerConfiguration;

    @Override
    public void sendHubEvent(HubEventAvro event) {
        ProducerRecord<String, HubEventAvro> record =
                new ProducerRecord<>(kafkaProducerConfiguration.getHubEventTopic(), event);

        log.debug("Sending sensor event {} to topic {}", event, record.topic());
        hubEventProducer.send(record);
    }

    @Override
    public void sendSensorEvent(SensorEventAvro event) {
        ProducerRecord<String, SensorEventAvro> record =
                new ProducerRecord<>(kafkaProducerConfiguration.getSensorEventTopic(), event);

        log.debug("Sending hub event {} to topic {}", event, record.topic());
        sensorEventProducer.send(record);
    }

}
