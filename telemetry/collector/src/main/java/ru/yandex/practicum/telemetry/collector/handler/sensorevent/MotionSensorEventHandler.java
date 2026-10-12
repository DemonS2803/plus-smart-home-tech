package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.MotionSensorEvent;

import java.time.Instant;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler {

    private final TelemetryService telemetryService;

    public MotionSensorEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Processing motion sensor event: {}", event);
        MotionSensorEvent entity = toMotionSensorEvent(event);
        telemetryService.registerSensorEvent(entity);
    }

    private MotionSensorEvent toMotionSensorEvent(SensorEventProto proto) {
        var motion = proto.getMotion();
        return new MotionSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                motion.getLinkQuality(),
                motion.getMotion(),
                motion.getVoltage()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
