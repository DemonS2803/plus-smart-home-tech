package ru.yandex.practicum.telemetry.collector.handler.hubevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.telemetry.collector.app.TelemetryService;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceAction;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioCondition;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final TelemetryService telemetryService;

    public ScenarioAddedEventHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("Processing scenario added hub event: {}", event);
        ScenarioAddedEvent entity = toScenarioAddedEvent(event);
        telemetryService.registerHubEvent(entity);
    }

    private ScenarioAddedEvent toScenarioAddedEvent(HubEventProto proto) {
        var scenarioAdded = proto.getScenarioAdded();
        List<ScenarioCondition> conditions = scenarioAdded.getConditionList().stream()
                .map(this::toScenarioCondition)
                .toList();

        List<DeviceAction> actions = scenarioAdded.getActionList().stream()
                .map(this::toDeviceAction)
                .toList();

        return new ScenarioAddedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                scenarioAdded.getName(),
                conditions,
                actions
        );
    }

    private ScenarioCondition toScenarioCondition(ScenarioConditionProto proto) {
        return new ScenarioCondition(
                proto.getSensorId(),
                proto.getType().name(),
                proto.getOperation().name(),
                toIntValue(proto)
        );
    }

    private DeviceAction toDeviceAction(DeviceActionProto proto) {
        return new DeviceAction(
                proto.getSensorId(),
                proto.getType().name(),
                proto.getValue()
        );
    }

    private Integer toIntValue(ScenarioConditionProto proto) {
        if (proto.hasIntValue()) {
            return proto.getIntValue();
        } else if (proto.hasBoolValue()) {
            return proto.getBoolValue() ? 1 : 0;
        }
        return null;
    }

    private Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
