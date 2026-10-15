package ru.yandex.practicum.telemetry.collector.handler.hubevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.app.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceAddedEvent;

import java.time.Instant;

@Slf4j
@Component
public class DeviceAddedEventHandler implements HubEventHandler {

    private final TelemetryService telemetryService;

    public DeviceAddedEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("Processing device added hub event: {}", event);
        DeviceAddedEvent entity = toDeviceAddedEvent(event);
        telemetryService.registerHubEvent(entity);
    }

    private DeviceAddedEvent toDeviceAddedEvent(HubEventProto proto) {
        var deviceAdded = proto.getDeviceAdded();
        return new DeviceAddedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                deviceAdded.getId(),
                deviceAdded.getType().name()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
