package ru.yandex.practicum.analyzer.service.handler.hubevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public HubEvent getEvent(HubEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();
        Instant timestamp = eventAvro.getTimestamp();
        DeviceRemovedEventAvro deviceRemovedAvro = (DeviceRemovedEventAvro) eventAvro.getPayload();
            return new DeviceRemovedEvent(
                hubId,
                timestamp,
                deviceRemovedAvro.getId()
            );
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void handle(HubEventAvro eventAvro) {
        DeviceRemovedEvent event = (DeviceRemovedEvent) getEvent(eventAvro);
        sensorRepository
                .findById(event.getId())
                .ifPresent(sensorRepository::delete);

        log.info("Device removed: id={}", event.getId());
    }

}
