package ru.yandex.practicum.telemetry.common.entity.sensor.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.telemetry.common.entity.sensor.BaseDevice;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotionSensor extends BaseDevice {

    private int linkQuality;
    private boolean motion;
    private int voltage;

}
