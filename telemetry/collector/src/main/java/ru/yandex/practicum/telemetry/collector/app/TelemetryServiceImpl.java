package ru.yandex.practicum.telemetry.collector.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.broker.TelemetryCollectorMessageBroker;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.serialization.avroschemas.mapper.HubEventAvroMapper;
import ru.yandex.practicum.telemetry.serialization.avroschemas.mapper.SensorEventAvroMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryCollectorMessageBroker broker;

    @Override
    public void registerHubEvent(HubEvent event) {
        log.info("Processing hub event: {}", event);
        HubEventAvro avroEvent = HubEventAvroMapper.toAvro(event);
        broker.sendHubEvent(avroEvent);
    }

    @Override
    public void registerSensorEvent(SensorEvent event) {
        log.info("Processing sensor event: {}", event);
        SensorEventAvro avroEvent = SensorEventAvroMapper.toAvro(event);
        broker.sendSensorEvent(avroEvent);
    }

}
