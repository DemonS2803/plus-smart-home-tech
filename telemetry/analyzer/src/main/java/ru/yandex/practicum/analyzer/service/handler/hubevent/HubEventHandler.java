package ru.yandex.practicum.analyzer.service.handler.hubevent;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;

public interface HubEventHandler {

    HubEventType getType();

    HubEvent getEvent(HubEventAvro eventAvro);

    void handle(HubEventAvro eventAvro);

}
