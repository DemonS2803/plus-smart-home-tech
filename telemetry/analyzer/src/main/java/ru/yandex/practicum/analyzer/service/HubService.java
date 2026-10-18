package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.service.handler.hubevent.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEventType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubService {

    private final Map<HubEventType, HubEventHandler> hubEventHandlerMap;

    @Autowired
    public HubService(Set<HubEventHandler> hubEventHandlers) {
        hubEventHandlerMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
        log.info("Created {} handlers for HubEvents", hubEventHandlerMap.size());
    }

    public void processEvent(HubEventAvro event) {
        HubEventType eventType = HubEventType.fromAvroClassName(event.getPayload().getClass().getName());
        HubEventHandler handler = hubEventHandlerMap.get(eventType);
        if (handler == null) {
            log.error("Not found handler for event: {} (incoming: {})", eventType, event.getPayload().getClass().getName());
            return;
        }

        handler.handle(event);
    }

}
