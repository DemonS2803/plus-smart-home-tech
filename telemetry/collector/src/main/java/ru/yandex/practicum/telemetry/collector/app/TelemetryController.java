package ru.yandex.practicum.telemetry.collector.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.common.utils.HttpConstants;

@Slf4j
@RequiredArgsConstructor
@RestController(HttpConstants.BASE_EVENTS_PATH)
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping(HttpConstants.HUB_EVENTS_PATH)
    public ResponseEntity<?> hubEvent(@RequestBody HubEvent event) {
        log.info("Get request for register hub event: {}", event);
        telemetryService.registerHubEvent(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(HttpConstants.SENSORS_EVENTS_PATH)
    public ResponseEntity<?> sensorEvent(@RequestBody SensorEvent event) {
        log.info("Get request for register sensor event: {}", event);
        telemetryService.registerSensorEvent(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
