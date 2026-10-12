package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.TemperatureSensorEvent;

import java.time.Instant;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final TelemetryService telemetryService;

    public TemperatureSensorEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Processing temperature sensor event: {}", event);
        TemperatureSensorEvent entity = toTemperatureSensorEvent(event);
        telemetryService.registerSensorEvent(entity);
    }

    private TemperatureSensorEvent toTemperatureSensorEvent(SensorEventProto proto) {
        var temperature = proto.getTemperature();
        return new TemperatureSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                temperature.getTemperatureC(),
                temperature.getTemperatureF()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
