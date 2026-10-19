package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Component
public class SwitchSensorValueHandler implements SensorValueHandler {

    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.SWITCH;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        SwitchSensorAvro switchState = (SwitchSensorAvro) state.getData();
        return switchState.getState() ? 1 : 0;
    }

}
