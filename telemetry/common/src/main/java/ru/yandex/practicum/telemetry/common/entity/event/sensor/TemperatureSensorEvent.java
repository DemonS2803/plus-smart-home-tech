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
@JsonTypeName("TEMPERATURE_SENSOR_EVENT")
public class TemperatureSensorEvent extends SensorEvent {

    private int temperatureC;
    private int temperatureF;

    public TemperatureSensorEvent(String id, String hubId, Instant timestamp, int temperatureC, int temperatureF) {
        super(id, hubId, timestamp);
        this.temperatureC = temperatureC;
        this.temperatureF = temperatureF;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

}
