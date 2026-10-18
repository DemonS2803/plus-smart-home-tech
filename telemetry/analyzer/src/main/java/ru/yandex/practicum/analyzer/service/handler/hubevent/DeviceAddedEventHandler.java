package ru.yandex.practicum.analyzer.service.handler.hubevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public HubEvent getEvent(HubEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();
        Instant timestamp = eventAvro.getTimestamp();
        DeviceAddedEventAvro deviceAddedAvro = (DeviceAddedEventAvro) eventAvro.getPayload();
        return new DeviceAddedEvent(
                hubId,
                timestamp,
                deviceAddedAvro.getId(),
                deviceAddedAvro.getType().name()
        );
    }

    @Override
    public void handle(HubEventAvro eventAvro) {
        DeviceAddedEvent event = (DeviceAddedEvent) getEvent(eventAvro);
        Sensor sensor = Sensor.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .build();
        sensorRepository.save(sensor);
        log.info("Device added: id={}, hubId={}, type={}",
                event.getId(), event.getHubId(), event.getDeviceType());

    }
}
