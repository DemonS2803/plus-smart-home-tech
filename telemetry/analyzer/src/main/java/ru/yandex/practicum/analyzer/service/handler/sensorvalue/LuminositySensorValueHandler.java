package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Slf4j
@Component
public class LuminositySensorValueHandler implements SensorValueHandler {


    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.LUMINOSITY;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        LightSensorAvro light = (LightSensorAvro) state.getData();
        return light.getLuminosity();
    }
}
