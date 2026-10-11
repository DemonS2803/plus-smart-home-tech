package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioCondition {

    private String sensorId;
    private String type;
    private String operation;
    private Integer value;

}
