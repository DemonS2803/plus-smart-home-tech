package ru.yandex.practicum.telemetry.collector.handler.sensorevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.app.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SwitchSensorEvent;

import java.time.Instant;

@Slf4j
@Component
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final TelemetryService telemetryService;

    public SwitchSensorEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Processing switch sensor event: {}", event);
        SwitchSensorEvent entity = toSwitchSensorEvent(event);
        telemetryService.registerSensorEvent(entity);
    }

    private SwitchSensorEvent toSwitchSensorEvent(SensorEventProto proto) {
        var switchSensor = proto.getSwitch();
        return new SwitchSensorEvent(
                proto.getId(),
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                switchSensor.getState()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
