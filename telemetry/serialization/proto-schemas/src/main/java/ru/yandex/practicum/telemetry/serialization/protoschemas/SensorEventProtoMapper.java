package ru.yandex.practicum.telemetry.serialization.protoschemas;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.*;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SensorEvent;

/**
 * Маппер для преобразования Protobuf-сообщений SensorEventProto в Entity-объекты.
 */
public class SensorEventProtoMapper {

    private SensorEventProtoMapper() {}

    public static SensorEvent toEntity(SensorEventProto proto) {
        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();
        switch (payloadCase) {
            case LIGHT:
                return toLightSensor(proto);
            case CLIMATE:
                return toClimateSensor(proto);
            case MOTION:
                return toMotionSensor(proto);
            case SWITCH:
                return toSwitchSensor(proto);
            case TEMPERATURE:
                return toTemperatureSensor(proto);
            default:
                throw new IllegalArgumentException("Unknown payload case: " + payloadCase);
        }
    }

    public static LightSensorEvent toLightSensor(SensorEventProto proto) {
        LightSensorProto light = proto.getLight();
        return new LightSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                light.getLinkQuality(),
                light.getLuminosity()
        );
    }

    public static ClimateSensorEvent toClimateSensor(SensorEventProto proto) {
        ClimateSensorProto climate = proto.getClimate();
        return new ClimateSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                climate.getTemperatureC(),
                climate.getHumidity(),
                climate.getCo2Level()
        );
    }

    public static MotionSensorEvent toMotionSensor(SensorEventProto proto) {
        MotionSensorProto motion = proto.getMotion();
        return new MotionSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                motion.getLinkQuality(),
                motion.getMotion(),
                motion.getVoltage()
        );
    }

    public static SwitchSensorEvent toSwitchSensor(SensorEventProto proto) {
        SwitchSensorProto switchSensor = proto.getSwitch();
        return new SwitchSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                switchSensor.getState()
        );
    }

    public static TemperatureSensorEvent toTemperatureSensor(SensorEventProto proto) {
        TemperatureSensorProto temperature = proto.getTemperature();
        return new TemperatureSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                temperature.getTemperatureC(),
                temperature.getTemperatureF()
        );
    }

    public static java.time.Instant toInstant(com.google.protobuf.Timestamp ts) {
        return java.time.Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
