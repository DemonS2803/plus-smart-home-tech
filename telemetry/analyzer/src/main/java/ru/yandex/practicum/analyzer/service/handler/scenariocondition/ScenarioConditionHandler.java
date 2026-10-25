package ru.yandex.practicum.analyzer.service.handler.scenariocondition;

import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

/**
 * Determine is scenario applicable for current sensors state
 */
public interface ScenarioConditionHandler {

    ConditionOperationAvro getType();

    /**
     * @param condition - saved condition from scenario
     * @param state - generalized incoming state of sensors
     * @return do we need to execute scenario action
     */
    boolean isApplicable(Condition condition, SensorStateAvro state);

}
