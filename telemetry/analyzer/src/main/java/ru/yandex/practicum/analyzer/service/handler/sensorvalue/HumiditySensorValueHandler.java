package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Component
public class HumiditySensorValueHandler implements SensorValueHandler {

    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.HUMIDITY;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        ClimateSensorAvro climate = (ClimateSensorAvro) state.getData();
        return climate.getHumidity();
    }
}
