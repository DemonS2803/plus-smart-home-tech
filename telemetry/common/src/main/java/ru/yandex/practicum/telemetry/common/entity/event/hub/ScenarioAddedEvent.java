package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("SCENARIO_ADDED")
public class ScenarioAddedEvent extends HubEvent {

    private String name;
    private List<ScenarioCondition> conditions;
    private List<DeviceAction> actions;

    public ScenarioAddedEvent(String hubId, Instant timestamp, String name, List<ScenarioCondition> conditions, List<DeviceAction> actions) {
        super();
        setHubId(hubId);
        setTimestamp(timestamp);
        this.name = name;
        this.conditions = conditions;
        this.actions = actions;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

}
