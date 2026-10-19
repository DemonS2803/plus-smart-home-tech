package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    private Condition condition;

    @Override
    public String toString() {
        return "ScenarioCondition{" +
                "id=" + id +
                ", condition=" + condition +
                '}';
    }
}
