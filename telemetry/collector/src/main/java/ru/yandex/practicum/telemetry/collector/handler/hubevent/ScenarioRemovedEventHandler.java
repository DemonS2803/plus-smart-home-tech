package ru.yandex.practicum.telemetry.collector.handler.hubevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.app.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioRemovedEvent;

import java.time.Instant;

@Slf4j
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final TelemetryService telemetryService;

    public ScenarioRemovedEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("Processing scenario removed hub event: {}", event);
        ScenarioRemovedEvent entity = toScenarioRemovedEvent(event);
        telemetryService.registerHubEvent(entity);
    }

    private ScenarioRemovedEvent toScenarioRemovedEvent(HubEventProto proto) {
        var scenarioRemoved = proto.getScenarioRemoved();
        return new ScenarioRemovedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                scenarioRemoved.getName()
        );
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
