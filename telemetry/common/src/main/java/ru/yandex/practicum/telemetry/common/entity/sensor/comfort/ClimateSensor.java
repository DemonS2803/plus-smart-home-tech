package ru.yandex.practicum.telemetry.common.entity.sensor.comfort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.yandex.practicum.telemetry.common.entity.sensor.BaseDevice;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClimateSensor extends BaseDevice {

    private int temperatureC;
    private int humidity;
    private int co2Level;

}
