package ru.yandex.practicum.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.*;
import ru.yandex.practicum.analyzer.service.handler.scenariocondition.ScenarioConditionHandler;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluates scenarios against sensor states and executes actions when conditions are met.
 * This service provides the core evaluation logic used by SnapshotProcessor and HubEventProcessor.
 */
@Slf4j
@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final Map<ConditionOperationAvro, ScenarioConditionHandler> scenarioConditionHandlers;
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Autowired
    public ScenarioService(
            ScenarioRepository scenarioRepository,
            SensorRepository sensorRepository,
            Set<ScenarioConditionHandler> scenarioConditionHandlers
    ) {
        this.scenarioRepository = scenarioRepository;
        this.sensorRepository = sensorRepository;
        this.scenarioConditionHandlers = scenarioConditionHandlers.stream()
                .collect(Collectors.toMap(
                        ScenarioConditionHandler::getType,
                        Function.identity()
                ));
    }

    public void processEvent(SensorsSnapshotAvro sensorsSnapshot) {
        String hubId = sensorsSnapshot.getHubId();
        Map<String, SensorStateAvro> sensorsStateMap = sensorsSnapshot.getSensorsState();

        log.info("Processing snapshot for hub {}, {} sensors. State: {}", hubId, sensorsStateMap.size(), sensorsSnapshot);

        for (Map.Entry<String, SensorStateAvro> entry : sensorsStateMap.entrySet()) {
            String sensorId = entry.getKey();
            saveSensor(sensorId, hubId);
        }

        Map<String, List<ScenarioAction>> sensorActions = getSensorActions(hubId, sensorsStateMap);

        log.info("Finished hub {} snapshot processing. Actions to do: {}", hubId, sensorActions);

        for (Map.Entry<String, List<ScenarioAction>> entry : sensorActions.entrySet()) {
            Set<ScenarioActionId> sentActions = new HashSet<>();
            for (ScenarioAction action : entry.getValue()) {
                if (!sentActions.contains(action.getId())) {
                    executeScenarioAction(action);
                }
                sentActions.add(action.getId());
            }
        }
    }

    private void saveSensor(String sensorId, String hubId) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElse(null);

        if (sensor == null) {
            sensorRepository.save(new Sensor(sensorId, hubId));
            log.info("New sensor registered: id={}, hubId={}", sensorId, hubId);
            // if hub changed
        } else if (!Objects.equals(sensor.getHubId(), hubId)) {
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    private void executeScenarioAction(ScenarioAction scenarioAction) {
        Scenario scenario = scenarioAction.getScenario();
        Action action = scenarioAction.getAction();
        Sensor sensor = scenarioAction.getSensor();

        log.info("Executing action {}", scenarioAction);

        // Use sensor from entity if available, otherwise fall back to composite key
        String sensorId = sensor != null ? sensor.getId() : scenarioAction.getId().getSensorId();

        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue() != null ? action.getValue() : 0)
                .build();

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(actionProto)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build()
                ).build();

        hubRouterClient.handleDeviceAction(request);

        log.info("Action executed successfully: hubId={}, scenario={}, sensor={}, type={}, action={}",
                scenario.getHubId(), scenario.getName(), sensorId, action.getType(), actionProto);
    }

    /**
     * @return map of what actions must do every sensor in this hub
     */
    public Map<String, List<ScenarioAction>> getSensorActions(String hubId, Map<String, SensorStateAvro> sensorStates) {
        Map<String, List<ScenarioAction>> sensorActionsMap = new HashMap<>();
        
        // run by all hub scenarios
        for (Scenario scenario : scenarioRepository.findByHubId(hubId)) {
            if (isScenarioApplicable(scenario, sensorStates)) {
                log.info("Scenario {} is valid, add actions {}", scenario.getName(), scenario.getActions());
                populateSensorActionsMap(scenario, sensorActionsMap);
            }
        }
        return sensorActionsMap;
    }

    private void populateSensorActionsMap(Scenario scenario, Map<String, List<ScenarioAction>> sensorActionsMap) {
        for (ScenarioAction action : scenario.getActions()) {
            // TODO: проверка на нулл нужна?
            sensorActionsMap.computeIfAbsent(
                    action.getSensor().getId(),
                    k -> new ArrayList<>()
            ).add(action);
        }
    }

    private boolean isScenarioApplicable(Scenario scenario, Map<String, SensorStateAvro> sensorStates) {
        boolean activate = true;

        log.info("Checking scenario {} with conditions {} is applicable to current state: {}",
                scenario.getName(), scenario.getConditions(), sensorStates);

        for (ScenarioCondition scenarioCondition : scenario.getConditions()) {
            // Use composite key sensorId if sensor entity reference is null
            String sensorId = scenarioCondition.getSensor() != null 
                    ? scenarioCondition.getSensor().getId() 
                    : scenarioCondition.getId().getSensorId();
            
            SensorStateAvro state = sensorStates.get(sensorId);
            
            if (state == null) {
                log.warn("Sensor with id={} not found in snapshot", sensorId);
                return false;
            }
            
            ScenarioConditionHandler handler = scenarioConditionHandlers
                    .get(scenarioCondition.getCondition().getOperation());

            activate &= handler.isApplicable(scenarioCondition.getCondition(), state);
        }
        return activate;
    }

}
