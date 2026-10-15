package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.app.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.ClimateSensorEvent;

import java.time.Instant;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final TelemetryService telemetryService;

    public ClimateSensorEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Processing climate sensor event: {}", event);
        ClimateSensorEvent entity = toClimateSensorEvent(event);
        telemetryService.registerSensorEvent(entity);
    }

    private ClimateSensorEvent toClimateSensorEvent(SensorEventProto proto) {
        var climate = proto.getClimate();
        return new ClimateSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                climate.getTemperatureC(),
                climate.getHumidity(),
                climate.getCo2Level()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
