package ru.yandex.practicum.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.analyzer.runner.HubEventProcessor;
import ru.yandex.practicum.analyzer.runner.SensorSnapshotProcessor;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class TelemetryAnalyzerApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(TelemetryAnalyzerApp.class, args);

        final HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        SensorSnapshotProcessor snapshotProcessor = context.getBean(SensorSnapshotProcessor.class);

        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        snapshotProcessor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Analyzer service");

            try {
                hubEventProcessor.getHubEventConsumer().wakeup();
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
