package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

/**
 * Интерфейс для обработки событий от датчиков.
 * Каждая реализация отвечает за свой тип датчика.
 */
public interface SensorEventHandler {

    /**
     * Возвращает тип payload, который обрабатывает данный хендлер.
     */
    SensorEventProto.PayloadCase getMessageType();

    /**
     * Обеспечивает обработку полученного события.
     */
    void handle(SensorEventProto event);
}
