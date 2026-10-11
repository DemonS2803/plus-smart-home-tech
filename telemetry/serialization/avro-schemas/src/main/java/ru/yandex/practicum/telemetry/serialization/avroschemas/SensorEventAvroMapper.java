package ru.yandex.practicum.telemetry.serialization.avroschemas;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.*;

public class SensorEventAvroMapper {

    public static SensorEventAvro toAvro(SensorEvent event) {
        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder();
        builder.setId(event.getId());
        builder.setHubId(event.getHubId());
        builder.setTimestamp(event.getTimestamp());

        switch (event.getType()) {
            case LIGHT_SENSOR_EVENT:
                builder.setPayload(toAvro((LightSensorEvent) event));
                break;
            case MOTION_SENSOR_EVENT:
                builder.setPayload(toAvro((MotionSensorEvent) event));
                break;
            case TEMPERATURE_SENSOR_EVENT:
                builder.setPayload(toAvro((TemperatureSensorEvent) event));
                break;
            case CLIMATE_SENSOR_EVENT:
                builder.setPayload(toAvro((ClimateSensorEvent) event));
                break;
            case SWITCH_SENSOR_EVENT:
                builder.setPayload(toAvro((SwitchSensorEvent) event));
                break;
        }

        return builder.build();
    }

    public static LightSensorAvro toAvro(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    public static MotionSensorAvro toAvro(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    public static ClimateSensorAvro toAvro(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    public static TemperatureSensorAvro toAvro(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }

    public static SwitchSensorAvro toAvro(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();
    }
}
