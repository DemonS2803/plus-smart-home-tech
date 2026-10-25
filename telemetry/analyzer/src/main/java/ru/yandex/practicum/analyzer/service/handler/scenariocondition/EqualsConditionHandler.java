package ru.yandex.practicum.analyzer.service.handler.scenariocondition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.handler.sensorvalue.SensorValueHandler;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import java.util.Objects;
import java.util.Set;

@Component
public class EqualsConditionHandler extends BaseConditionHandler {

    @Autowired
    public EqualsConditionHandler(Set<SensorValueHandler> valueHandlers) {
        super(valueHandlers);
    }

    @Override
    protected boolean isApplicable(Integer current, Integer target) {
        return Objects.equals(current, target);
    }

    @Override
    public ConditionOperationAvro getType() {
        return ConditionOperationAvro.EQUALS;
    }

}
