package ru.yandex.practicum.telemetry.common.entity.event.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAction {

    private String sensorId;
    private String type;
    private Integer value;

}
