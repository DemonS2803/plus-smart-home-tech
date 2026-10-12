package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("DEVICE_ADDED")
public class DeviceAddedEvent extends HubEvent {

    private String id;
    private String deviceType;

    public DeviceAddedEvent(String hubId, Instant timestamp, String id, String deviceType) {
        super();
        setHubId(hubId);
        setTimestamp(timestamp);
        this.id = id;
        this.deviceType = deviceType;
    }

    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

}
