package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioConditionId implements Serializable {

    private Long scenarioId;
    private String sensorId;
    private Long conditionId;

}
