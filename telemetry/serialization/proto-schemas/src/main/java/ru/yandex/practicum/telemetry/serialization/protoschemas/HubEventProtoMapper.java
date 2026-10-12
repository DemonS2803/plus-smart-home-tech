package ru.yandex.practicum.telemetry.serialization.protoschemas;

import java.util.List;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.common.entity.event.hub.*;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;

/**
 * Маппер для преобразования Protobuf-сообщений HubEventProto в Entity-объекты.
 */
public class HubEventProtoMapper {

    private HubEventProtoMapper() {}

    public static HubEvent toEntity(HubEventProto proto) {
        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();
        return switch (payloadCase) {
            case DEVICE_ADDED -> toDeviceAddedEvent(proto);
            case DEVICE_REMOVED -> toDeviceRemovedEvent(proto);
            case SCENARIO_ADDED -> toScenarioAddedEvent(proto);
            case SCENARIO_REMOVED -> toScenarioRemovedEvent(proto);
            default -> throw new IllegalArgumentException("Unknown payload case: " + payloadCase);
        };
    }

    public static DeviceAddedEvent toDeviceAddedEvent(HubEventProto proto) {
        DeviceAddedEventProto deviceAdded = proto.getDeviceAdded();
        return new DeviceAddedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                deviceAdded.getId(),
                deviceAdded.getType().name()
        );
    }

    public static DeviceRemovedEvent toDeviceRemovedEvent(HubEventProto proto) {
        DeviceRemovedEventProto deviceRemoved = proto.getDeviceRemoved();
        return new DeviceRemovedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                deviceRemoved.getId()
        );
    }

    public static ScenarioAddedEvent toScenarioAddedEvent(HubEventProto proto) {
        ScenarioAddedEventProto scenarioAdded = proto.getScenarioAdded();
        List<ScenarioCondition> conditions = scenarioAdded.getConditionList().stream()
                .map(HubEventProtoMapper::toScenarioCondition)
                .toList();

        List<DeviceAction> actions = scenarioAdded.getActionList().stream()
                .map(HubEventProtoMapper::toDeviceAction)
                .toList();

        return new ScenarioAddedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                scenarioAdded.getName(),
                conditions,
                actions
        );
    }

    public static ScenarioRemovedEvent toScenarioRemovedEvent(HubEventProto proto) {
        ScenarioRemovedEventProto scenarioRemoved = proto.getScenarioRemoved();
        return new ScenarioRemovedEvent(
                proto.getHubId(),
                toInstant(proto.getTimestamp()),
                scenarioRemoved.getName()
        );
    }

    public static ScenarioCondition toScenarioCondition(ScenarioConditionProto proto) {
        return new ScenarioCondition(
                proto.getSensorId(),
                proto.getType().name(),
                proto.getOperation().name(),
                toIntValue(proto)
        );
    }

    public static DeviceAction toDeviceAction(DeviceActionProto proto) {
        return new DeviceAction(
                proto.getSensorId(),
                proto.getType().name(),
                proto.getValue()
        );
    }

    private static Integer toIntValue(ScenarioConditionProto proto) {
        if (proto.hasIntValue()) {
            return proto.getIntValue();
        } else if (proto.hasBoolValue()) {
            return proto.getBoolValue() ? 1 : 0;
        }
        return null;
    }

    private static java.time.Instant toInstant(com.google.protobuf.Timestamp ts) {
        return java.time.Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
