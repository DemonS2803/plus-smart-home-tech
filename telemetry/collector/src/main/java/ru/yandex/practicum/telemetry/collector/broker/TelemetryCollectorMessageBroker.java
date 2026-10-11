package ru.yandex.practicum.telemetry.collector.broker;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

/**
 * This is class for sending msgs from telemetry-collector to
 * message broker (kafka, rabbitmq, etc...)
 */
public interface TelemetryCollectorMessageBroker {

    void sendHubEvent(HubEventAvro event);

    void sendSensorEvent(SensorEventAvro event);

}
