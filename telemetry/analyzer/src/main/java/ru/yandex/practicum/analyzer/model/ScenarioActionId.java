package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioActionId implements Serializable {

    private Long scenarioId;
    private String sensorId;
    private Long actionId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScenarioActionId that = (ScenarioActionId) o;
        return Objects.equals(scenarioId, that.scenarioId) && Objects.equals(sensorId, that.sensorId) && Objects.equals(actionId, that.actionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioId, sensorId, actionId);
    }

    @Override
    public String toString() {
        return "ScenarioActionId{" +
                "scenarioId=" + scenarioId +
                ", sensorId='" + sensorId + '\'' +
                ", actionId=" + actionId +
                '}';
    }
}
