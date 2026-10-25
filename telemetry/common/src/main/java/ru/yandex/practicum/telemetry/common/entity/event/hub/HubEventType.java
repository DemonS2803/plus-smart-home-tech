package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public enum HubEventType {

    DEVICE_ADDED,
    DEVICE_REMOVED,
    SCENARIO_ADDED,
    SCENARIO_REMOVED;

    /**
     * "EVENTTYPE" -> EVENT_TYPE
     */
    private static Map<String, HubEventType> classNameToEventMap;

    static {
        classNameToEventMap = Arrays.stream(values())
                .collect(Collectors.toMap(
                        type -> type.name().replaceAll("_", ""),
                        Function.identity()
                ));
    }

    // pretty stupid mapper :-)
    /**
     * Map class with EventType suffix to enum entity
     *
     * DeviceAddedEventType -> DEVICE_ADDED type
     * ru.yandex.practicum.DeviceAddedEventType -> DEVICE_ADDED type
     */
    public static HubEventType fromAvroClassName(String className) {
        if (className.contains(".")) {
            String[] splitted = className.split("\\.");
            className = splitted[splitted.length - 1];
        }
        String searchKey = className.replace("EventAvro", "").toUpperCase();
        HubEventType type = classNameToEventMap.get(searchKey);

        if (type == null) {
            log.error("Unknown class name for hub event: {}", className);
        }

        return type;
    }

}
