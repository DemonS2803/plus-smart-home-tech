package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonTypeName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("DEVICE_REMOVED")
public class DeviceRemovedEvent extends HubEvent {

    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }

}
