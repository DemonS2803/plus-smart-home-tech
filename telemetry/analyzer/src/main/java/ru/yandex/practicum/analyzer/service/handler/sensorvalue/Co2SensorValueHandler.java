package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Slf4j
@Component
public class Co2SensorValueHandler implements SensorValueHandler {

    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.CO2LEVEL;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        ClimateSensorAvro climate = (ClimateSensorAvro) state.getData();
        return climate.getCo2Level();
    }
}
