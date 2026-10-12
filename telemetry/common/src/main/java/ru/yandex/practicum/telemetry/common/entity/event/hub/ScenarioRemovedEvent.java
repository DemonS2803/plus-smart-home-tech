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
@JsonTypeName("SCENARIO_REMOVED")
public class ScenarioRemovedEvent extends HubEvent {

    private String name;

    public ScenarioRemovedEvent(String hubId, Instant timestamp, String name) {
        super();
        setHubId(hubId);
        setTimestamp(timestamp);
        this.name = name;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }

}
