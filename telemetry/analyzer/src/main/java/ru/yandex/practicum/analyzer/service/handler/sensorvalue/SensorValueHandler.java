package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

/**
 * Get int value presentation from sensor snapshot by scenario condition
 */
public interface SensorValueHandler {

    ConditionTypeAvro getType();

    Integer getValue(SensorStateAvro state);

}
