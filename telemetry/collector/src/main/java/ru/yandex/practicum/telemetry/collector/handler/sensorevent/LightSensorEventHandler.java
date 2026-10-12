package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.LightSensorEvent;

import java.time.Instant;

@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    private final TelemetryService telemetryService;

    public LightSensorEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Processing light sensor event: {}", event);
        LightSensorEvent entity = toLightSensorEvent(event);
        telemetryService.registerSensorEvent(entity);
    }

    private LightSensorEvent toLightSensorEvent(SensorEventProto proto) {
        var light = proto.getLight();
        return new LightSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                light.getLinkQuality(),
                light.getLuminosity()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
