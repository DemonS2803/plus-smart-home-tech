package ru.yandex.practicum.analyzer.service.handler.scenariocondition;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.service.handler.sensorvalue.SensorValueHandler;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseConditionHandler implements ScenarioConditionHandler {

    Map<ConditionTypeAvro, SensorValueHandler> valueHandlerMap;

    public BaseConditionHandler(Set<SensorValueHandler> valueHandlers) {
        valueHandlerMap = valueHandlers.stream()
            .collect(Collectors.toMap(
                    SensorValueHandler::getType,
                    Function.identity()
            ));
    }

    @Override
    public boolean isApplicable(Condition condition, SensorStateAvro state) {
        ConditionTypeAvro conditionType = condition.getType();
        SensorValueHandler handler = valueHandlerMap.get(conditionType);

        if (!conditionType.equals(handler.getType())) {
            log.debug("Selected sensor type {} mismatches with condition type {}",
                    handler.getType(), conditionType);
            return false;
        }

        Integer target = condition.getValue();
        Integer current = handler.getValue(state);

        return isApplicable(current, target);
    }

    protected abstract boolean isApplicable(Integer current, Integer target);

}
