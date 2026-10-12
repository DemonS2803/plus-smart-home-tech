package ru.yandex.practicum.telemetry.collector.handler.hubevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.service.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceRemovedEvent;

import java.time.Instant;

@Slf4j
@Component
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final TelemetryService telemetryService;

    public DeviceRemovedEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("Processing device removed hub event: {}", event);
        DeviceRemovedEvent entity = toDeviceRemovedEvent(event);
        telemetryService.registerHubEvent(entity);
    }

    private DeviceRemovedEvent toDeviceRemovedEvent(HubEventProto proto) {
        var deviceRemoved = proto.getDeviceRemoved();
        return new DeviceRemovedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                deviceRemoved.getId()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
