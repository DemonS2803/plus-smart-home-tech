package ru.yandex.practicum.telemetry.serialization.avroschemas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.common.entity.event.hub.*;

/**
 * Можно было бы перетащить serialization в common модуль и
 * переместить мапперы в пакет мапперов, но пока нет надобности
 */
public class HubEventAvroMapper {

    public static HubEventAvro toAvro(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder();
        builder.setHubId(event.getHubId());
        builder.setTimestamp(event.getTimestamp());

        switch (event.getType()) {
            case DEVICE_ADDED:
                builder.setPayload(toAvro((DeviceAddedEvent) event));
                break;
            case DEVICE_REMOVED:
                builder.setPayload(toAvro((DeviceRemovedEvent) event));
                break;
            case SCENARIO_ADDED:
                builder.setPayload(toAvro((ScenarioAddedEvent) event));
                break;
            case SCENARIO_REMOVED:
                builder.setPayload(toAvro((ScenarioRemovedEvent) event));
                break;
        }

        return builder.build();
    }

    public static DeviceAddedEventAvro toAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType()))
                .build();
    }

    public static DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public static ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event) {
        List<ScenarioConditionAvro> conditions = event.getConditions().stream()
                .map(HubEventAvroMapper::toAvroCondition)
                .collect(Collectors.toList());

        List<DeviceActionAvro> actions = event.getActions().stream()
                .map(HubEventAvroMapper::toAvroAction)
                .collect(Collectors.toList());

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    public static ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public static ScenarioConditionAvro toAvroCondition(ScenarioCondition condition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation()));

        if (condition.getValue() == null) {
            builder.setValue(null);
        } else if (condition.getValue() >= 0 && condition.getValue() <= 1) {
            builder.setValue(condition.getValue() == 1);
        } else {
            builder.setValue(condition.getValue());
        }

        return builder.build();
    }

    public static DeviceActionAvro toAvroAction(DeviceAction action) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType()));

        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }
}
