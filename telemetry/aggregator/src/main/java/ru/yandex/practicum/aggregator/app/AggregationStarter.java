package ru.yandex.practicum.aggregator.app;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.common.utils.KafkaConstants;
import ru.yandex.practicum.telemetry.serialization.avroschemas.deserialize.SensorEventDeserializer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final Logger log = LoggerFactory.getLogger(AggregationStarter.class);

    private final KafkaConsumer<String, SensorEventAvro> sensorEventConsumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> sensorSnapshotProducer;
    private final Map<String, SensorsSnapshotAvro> hubSnapshotsMap = new ConcurrentHashMap<>();

    public void start() {
        try {
            log.info("Starting Aggregator service...");

            sensorEventConsumer.subscribe(
                    List.of(KafkaConstants.TOPIC_TELEMETRY_SENSORS_V1)
            );

            // вроде в уроке про многопоточку учили так
            while (!Thread.currentThread().isInterrupted()) {
                var records = sensorEventConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(record.value());

                    if (updatedSnapshot.isPresent()) {
                        sensorSnapshotProducer.send(new ProducerRecord<>(
                                KafkaConstants.TOPIC_TELEMETRY_SNAPSHOTS_V1,
                                record.value().getHubId(),
                                updatedSnapshot.get()
                        ));
                    }
                }

                sensorEventConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("Handle kafka wakeup event");
        } catch (Exception e) {
            log.error("Error during sensor event processing", e);
            throw e;
        } finally {
            log.info("Shutting down Aggregator service...");
            try {
                sensorSnapshotProducer.flush();
                sensorEventConsumer.commitSync();

                sensorEventConsumer.close();
                sensorSnapshotProducer.close();

                log.info("Aggregator service shutdown complete");
            } catch (Exception e) {
                log.error("Error during shutdown", e);
            }
        }
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();
        String deviceId = event.getId();
        Instant eventTimestamp = event.getTimestamp();

        SensorsSnapshotAvro snapshot = hubSnapshotsMap.computeIfAbsent(hubId, id -> {
            return SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(eventTimestamp)
                    .setSensorsState(new HashMap<>())
                    .build();
        });

        SensorStateAvro oldState = snapshot.getSensorsState().get(deviceId);

        // if we have newer data
        if (oldState != null && oldState.getTimestamp().isAfter(eventTimestamp)) {
            log.debug("Ignoring outdated event for device {} in hub {}: old={}, new={}",
                    deviceId, hubId, oldState.getTimestamp(), eventTimestamp);
            return Optional.empty();
        }

        // duplicate
        if (oldState != null && oldState.getData().equals(event.getPayload())) {
            log.debug("Ignoring duplicate event for device {} in hub {}", deviceId, hubId);
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(eventTimestamp)
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(deviceId, newState);
        snapshot.setTimestamp(eventTimestamp);

        hubSnapshotsMap.put(hubId, snapshot);

        log.debug("Updated snapshot for hub {} with device {}", hubId, deviceId);
        return Optional.of(snapshot);
    }

}
