package ru.yandex.practicum.telemetry.collector.handler.hubevent;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

/**
 * Интерфейс для обработки событий хаба.
 * Каждая реализация отвечает за свой тип события.
 */
public interface HubEventHandler {

    /**
     * Возвращает тип payload, который обрабатывает данный хендлер.
     */
    HubEventProto.PayloadCase getMessageType();

    /**
     * Обеспечивает обработку полученного события.
     */
    void handle(HubEventProto event);
}
