package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Slf4j
@Component
public class TemperatureSensorValueHandler implements SensorValueHandler {

    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.TEMPERATURE;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        if (state.getData() instanceof TemperatureSensorAvro temperature) {
            return temperature.getTemperatureC();
        }
        if (state.getData() instanceof ClimateSensorAvro climate) {
            return climate.getTemperatureC();
        }
        log.warn("Unknown temperature sensor type: {}", state.getData().getClass().getSimpleName());
        return null;
    }

}
