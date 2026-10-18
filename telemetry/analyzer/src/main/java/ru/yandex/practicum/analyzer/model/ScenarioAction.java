package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    // не спрашивайте, зачем такой маппинг
    // сущностей, просто захотелось сделать
    // 1 в 1 с таблицами
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
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    private Action action;

    @Override
    public String toString() {
        return "ScenarioAction{" +
                "id=" + id +
                ", action=" + action +
                '}';
    }
}
