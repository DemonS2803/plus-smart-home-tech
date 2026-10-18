package ru.yandex.practicum.analyzer.service.handler.hubevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioRemovedEvent;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler  implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public HubEvent getEvent(HubEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();
        Instant timestamp = eventAvro.getTimestamp();
        ScenarioRemovedEventAvro scenarioRemovedAvro = (ScenarioRemovedEventAvro) eventAvro.getPayload();
                return new ScenarioRemovedEvent(
                    hubId, timestamp,
                    scenarioRemovedAvro.getName()
                );
    }

    @Override
    public void handle(HubEventAvro eventAvro) {
        ScenarioRemovedEvent event = (ScenarioRemovedEvent) getEvent(eventAvro);

        scenarioRepository
                .findByHubIdAndName(event.getHubId(), event.getName())
                .ifPresent(scenarioRepository::delete);

        log.info("Scenario removed: name={}, hubId={}", event.getName(), event.getHubId());
   }

}
