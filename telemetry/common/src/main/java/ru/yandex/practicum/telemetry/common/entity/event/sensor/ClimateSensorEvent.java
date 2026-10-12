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
@JsonTypeName("CLIMATE_SENSOR_EVENT")
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;
    private int humidity;
    private int co2Level;

    public ClimateSensorEvent(String id, String hubId, Instant timestamp, int temperatureC, int humidity, int co2Level) {
        super(id, hubId, timestamp);
        this.temperatureC = temperatureC;
        this.humidity = humidity;
        this.co2Level = co2Level;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

}
