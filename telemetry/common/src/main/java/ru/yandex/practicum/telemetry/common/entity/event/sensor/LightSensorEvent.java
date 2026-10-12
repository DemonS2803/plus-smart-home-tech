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
@JsonTypeName("LIGHT_SENSOR_EVENT")
public class LightSensorEvent extends SensorEvent {

    private int linkQuality;
    private int luminosity;

    public LightSensorEvent(String id, String hubId, Instant timestamp, int linkQuality, int luminosity) {
        super(id, hubId, timestamp);
        this.linkQuality = linkQuality;
        this.luminosity = luminosity;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

}
