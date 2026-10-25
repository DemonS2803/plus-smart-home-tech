package ru.yandex.practicum.aggregator.app;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.broker.KafkaProducerConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.common.utils.KafkaConstants;

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
    private final KafkaProducerConfig sensorSnapshotProducerConfig;
    /**
     * Mapping hubId -> SensorsSnapshot[
     *      sens1 -> SensorState,
     *      sens2 -> SensorState
     * ]
     */
    private final Map<String, SensorsSnapshotAvro> hubSnapshotsMap = new ConcurrentHashMap<>();

    public void start() {
        try {
            log.info("Starting Aggregator service...");
            String snapshotsTopic = sensorSnapshotProducerConfig.getSnapshotsTopicName();

            // вроде в уроке про многопоточку учили так
            while (!Thread.currentThread().isInterrupted()) {
                var records = sensorEventConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(record.value());

                    if (updatedSnapshot.isPresent()) {
                        sensorSnapshotProducer.send(new ProducerRecord<>(
                                snapshotsTopic,
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
        if (oldState != null && isEqual(oldState.getData(), event.getPayload())) {
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

        log.info("Updated snapshot for hub {} with device {} (current size {}): {}",
                hubId, deviceId, snapshot.getSensorsState().size(), snapshot
        );
        return Optional.of(snapshot);
    }

    public static boolean isEqual(Object data1, Object data2) {
        log.debug("Compare snapshot for {}", data1.getClass());

        if (data1 == data2) return true;

        if (data1 instanceof SpecificRecord record1 &&data2 instanceof SpecificRecord record2) {

            if (!record1.getSchema().equals(record2.getSchema())) return false;

            // Сравнение всех полей по порядку
            for (int i = 0; i < record1.getSchema().getFields().size(); i++) {
                Object val1 = record1.get(i);
                Object val2 = record2.get(i);
                if (!Objects.deepEquals(val1, val2)) return false;
            }
            return true;
        }
        return false;
    }

}
