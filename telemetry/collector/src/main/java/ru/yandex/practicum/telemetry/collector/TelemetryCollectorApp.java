package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TelemetryCollectorApp {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryCollectorApp.class, args);
    }

}
