package ru.yandex.practicum.analyzer.runner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.HubService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Getter
    private final KafkaConsumer<String, HubEventAvro> hubEventConsumer;
    private final HubService hubService;

    @Override
    public void run() {
        log.info("Hub event processor started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Iterable<ConsumerRecord<String, HubEventAvro>> records =
                        hubEventConsumer.poll(Duration.ofSeconds(1));
                    processRecords(records);

                } catch (Exception e) {
                    log.error("Error in hub event processor loop: {}", e.getMessage(), e);
                }
            }
        } finally {
            hubEventConsumer.wakeup();
            hubEventConsumer.close();
        }
    }

    private void processRecords(Iterable<ConsumerRecord<String, HubEventAvro>> records) {
        for (ConsumerRecord<String, HubEventAvro> record : records) {
            try {
                hubService.processEvent(record.value());
                hubEventConsumer.commitSync(
                        Collections.singletonMap(
                                new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1, null)
                        )
                );
            } catch (Exception e) {
                log.error("Error processing hub event: {}", e.getMessage(), e);
            }
        }
    }

}
