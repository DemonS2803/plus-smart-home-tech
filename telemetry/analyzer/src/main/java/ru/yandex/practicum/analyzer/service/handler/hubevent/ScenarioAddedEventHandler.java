package ru.yandex.practicum.analyzer.service.handler.hubevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioCondition;
import ru.yandex.practicum.telemetry.common.entity.event.hub.DeviceAction;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;
import ru.yandex.practicum.telemetry.common.entity.event.hub.ScenarioAddedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;


    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public HubEvent getEvent(HubEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();
        Instant timestamp = eventAvro.getTimestamp();
        ScenarioAddedEventAvro scenarioAddedAvro = (ScenarioAddedEventAvro) eventAvro.getPayload();

        List<ScenarioCondition> conditions =
                scenarioAddedAvro.getConditions().stream()
                    .map(this::fromAvroCondition)
                    .collect(Collectors.toList());
        List<DeviceAction> actions = scenarioAddedAvro.getActions().stream()
            .map(this::fromAvroAction)
            .collect(Collectors.toList());

        return new ScenarioAddedEvent(
            hubId, timestamp,
            scenarioAddedAvro.getName(),
            conditions,
            actions
        );
    }

    private ScenarioCondition fromAvroCondition(ScenarioConditionAvro avroCondition) {
        Integer value = null;
        Object val = avroCondition.getValue();
        if (val instanceof Boolean) {
            value = ((boolean) val) ? 1 : 0;
        } else if (val instanceof Integer) {
            value = (Integer) val;
        }

        return new ScenarioCondition(
                avroCondition.getSensorId(),
                avroCondition.getType().name(),
                avroCondition.getOperation().name(),
                value
        );
    }

    private DeviceAction fromAvroAction(DeviceActionAvro avroAction) {
        return new DeviceAction(
                avroAction.getSensorId(),
                avroAction.getType().name(),
                ((Number) avroAction.getValue()).intValue()
        );
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void handle(HubEventAvro eventAvro) {
        ScenarioAddedEvent event = (ScenarioAddedEvent) getEvent(eventAvro);

        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(
            event.getHubId(), event.getName()
        );

        if (existingScenario.isPresent()) {
            Scenario scenario = existingScenario.get();

            updateScenario(scenario, event);
            log.info("Scenario updated: name={}, hubId={}", event.getName(), event.getHubId());
        } else {
            // Create new scenario
            Scenario scenario = Scenario.builder()
                .hubId(event.getHubId())
                .conditions(new ArrayList<>())
                .actions(new ArrayList<>())
                .name(event.getName())
                .build();
            scenario = scenarioRepository.save(scenario);

            updateScenario(scenario, event);
            log.info("Scenario created: name={}, hubId={}", event.getName(), event.getHubId());
        }
    }

    private void updateScenario(Scenario scenario, ScenarioAddedEvent event) {
        // clean existing
        scenario.getConditions().clear();
        scenario.getActions().clear();

        // build new
        List<Condition> conditions = event.getConditions().stream()
                .map(condition -> Condition.builder()
                        .type(ConditionTypeAvro.valueOf(condition.getType()))
                        .operation(ConditionOperationAvro.valueOf(condition.getOperation()))
                        .value(condition.getValue())
                        .build()
                ).collect(Collectors.toList());
        List<Action> actions = event.getActions().stream()
                .map(action -> Action.builder()
                        .type(ActionTypeAvro.valueOf(action.getType()))
                        .value(action.getValue())
                        .build()
                ).collect(Collectors.toList());

        // save
        List<Condition> savedConditions = conditionRepository.saveAll(conditions);
        List<Action> savedActions = actionRepository.saveAll(actions);

        // cache for linking logic
        // for all hub sensors:
        // sensorId -> SensorEntity
        Map<String, Sensor> hubSensorsMap = sensorRepository.findSensorsByHubId(scenario.getHubId())
                .stream()
                .collect(Collectors.toMap(
                        Sensor::getId,
                        Function.identity()
                ));

        for (int i = 0; i < event.getConditions().size(); i++) {
            // incoming request
            ScenarioCondition scenarioCondition = event.getConditions().get(i);
            // DB data
            Condition condition = savedConditions.get(i);
            ScenarioConditionId conditionId = ScenarioConditionId.builder()
                    .scenarioId(scenario.getId())
                    .sensorId(scenarioCondition.getSensorId())
                    .conditionId(condition.getId())
                    .build();

            // namespace conflict :-(
            ru.yandex.practicum.analyzer.model.ScenarioCondition conditionLink = ru.yandex.practicum.analyzer.model.ScenarioCondition.builder()
                    .id(conditionId)
                    .scenario(scenario)
                    .sensor(hubSensorsMap.get(scenarioCondition.getSensorId()))
                    .condition(condition)
                    .build();
            scenario.getConditions().add(conditionLink);
        }

        for (int i = 0; i < event.getActions().size(); i++) {
            // incoming request
            DeviceAction deviceAction = event.getActions().get(i);
            // DB data
            Action action = savedActions.get(i);
            ScenarioActionId actionId = ScenarioActionId.builder()
                    .scenarioId(scenario.getId())
                    .sensorId(deviceAction.getSensorId())
                    .actionId(action.getId())
                    .build();
            ScenarioAction actionLink = ScenarioAction.builder()
                    .id(actionId)
                    .scenario(scenario)
                    .sensor(hubSensorsMap.get(deviceAction.getSensorId()))
                    .action(action)
                    .build();
            scenario.getActions().add(actionLink);
        }

        scenarioRepository.save(scenario);
    }

}