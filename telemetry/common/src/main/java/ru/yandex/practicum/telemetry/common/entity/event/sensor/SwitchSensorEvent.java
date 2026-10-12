package ru.yandex.practicum.telemetry.common.entity.event.sensor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("SWITCH_SENSOR_EVENT")
public class SwitchSensorEvent extends SensorEvent {

    private boolean state;

    public SwitchSensorEvent(String id, String hubId, Instant timestamp, boolean state) {
        super(id, hubId, timestamp);
        this.state = state;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
