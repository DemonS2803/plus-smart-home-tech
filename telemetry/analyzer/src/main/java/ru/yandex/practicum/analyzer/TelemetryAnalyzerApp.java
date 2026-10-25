package ru.yandex.practicum.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.analyzer.runner.HubEventRunner;
import ru.yandex.practicum.analyzer.runner.SensorSnapshotRunner;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class TelemetryAnalyzerApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(TelemetryAnalyzerApp.class, args);

        final HubEventRunner hubEventRunner = context.getBean(HubEventRunner.class);
        SensorSnapshotRunner snapshotProcessor = context.getBean(SensorSnapshotRunner.class);

        Thread hubEventsThread = new Thread(hubEventRunner);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        snapshotProcessor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Analyzer service");

            try {
                hubEventRunner.getHubEventConsumer().wakeup();
                hubEventsThread.join(5000);
            } catch (InterruptedException e) {
                hubEventsThread.interrupt();
            }

            try {
                snapshotProcessor.getSnapshotConsumer().wakeup();
                snapshotProcessor.join(5000);
            } catch (InterruptedException e) {
                snapshotProcessor.interrupt();
            }

            context.close();
            log.info("Analyzer service shutdown complete");
        }));

        log.info("Analyzer service started successfully");
    }

}
