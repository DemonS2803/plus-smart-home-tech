package ru.yandex.practicum.analyzer.service.handler.sensorvalue;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Component
public class MotionSensorValueHandler implements SensorValueHandler {


    @Override
    public ConditionTypeAvro getType() {
        return ConditionTypeAvro.MOTION;
    }

    @Override
    public Integer getValue(SensorStateAvro state) {
        MotionSensorAvro motion = (MotionSensorAvro) state.getData();
        return motion.getMotion() ? 1 : 0;
    }
}
