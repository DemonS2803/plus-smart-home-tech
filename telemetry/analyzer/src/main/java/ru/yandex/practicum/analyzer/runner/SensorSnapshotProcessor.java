package ru.yandex.practicum.analyzer.runner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.ScenarioService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Component
@RequiredArgsConstructor
public class SensorSnapshotProcessor extends Thread {

    @Getter
    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioService scenarioService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void run() {
        log.info("Sensor snapshot processor started");
        running.set(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered, stopping sensor snapshot processor");
            running.set(false);
            snapshotConsumer.wakeup();
        }, "snapshot-processor-shutdown-hook"));

        try {
            while (!Thread.currentThread().isInterrupted() && running.get()) {
                try {
                    Iterable<ConsumerRecord<String, SensorsSnapshotAvro>> records =
                        snapshotConsumer.poll(Duration.ofSeconds(1));
                        processRecords(records);
                } catch (WakeupException e) {
                    log.info("WakeupException caught, shutting down gracefully");
                    break;
                } catch (Exception e) {
                    log.error("Error in sensor snapshot processor loop: {}", e.getMessage(), e);
                }
            }
        } finally {
            if (running.get()) {
                snapshotConsumer.wakeup();
            }
            snapshotConsumer.close();
            running.set(false);
            log.info("Sensor snapshot processor stopped");
        }
    }

    @Override
    public void interrupt() {
        running.set(false);
        snapshotConsumer.wakeup();
        super.interrupt();
    }

    private void processRecords(Iterable<ConsumerRecord<String, SensorsSnapshotAvro>> records) {
        for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
            scenarioService.processEvent(record.value());
            snapshotConsumer.commitSync(
                    Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, null)
                    )
            );
        }
    }

}
