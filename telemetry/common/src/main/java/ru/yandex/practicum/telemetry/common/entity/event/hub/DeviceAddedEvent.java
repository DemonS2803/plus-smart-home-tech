package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonTypeName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("DEVICE_ADDED")
public class DeviceAddedEvent extends HubEvent {

    private String id;
    private String deviceType;

    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

}
