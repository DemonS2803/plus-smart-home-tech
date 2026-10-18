package ru.yandex.practicum.analyzer.service.handler.hubaction;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

/**
 * Handle hub action
 */
public interface HubActionHandler<T extends Enum> {

    T getType();

    /**
     * Execute some business logic for this event
     */
    void handle();

}
