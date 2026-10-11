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
@JsonTypeName("TEMPERATURE_SENSOR_EVENT")
public class TemperatureSensorEvent extends SensorEvent {

    private int temperatureC;
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

}
