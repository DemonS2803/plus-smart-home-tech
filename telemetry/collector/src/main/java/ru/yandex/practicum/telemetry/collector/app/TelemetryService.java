package ru.yandex.practicum.telemetry.collector.app;

import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SensorEvent;

public interface TelemetryService {

    void registerHubEvent(HubEvent event);

    void registerSensorEvent(SensorEvent event);

}
