package ru.yandex.practicum.telemetry.common.entity.event.sensor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("SWITCH_SENSOR_EVENT")
public class SwitchSensorEvent extends SensorEvent {

    private boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
