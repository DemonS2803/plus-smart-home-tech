package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("DEVICE_REMOVED")
public class DeviceRemovedEvent extends HubEvent {

    private String id;

    public DeviceRemovedEvent(String hubId, Instant timestamp, String id) {
        super();
        setHubId(hubId);
        setTimestamp(timestamp);
        this.id = id;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }

}
