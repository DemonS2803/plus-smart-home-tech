package ru.yandex.practicum.analyzer.service.handler.hubaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Slf4j
@Component
public class InverseHubActionHandler
        implements HubActionHandler<ActionTypeAvro> {


    @Override
    public ActionTypeAvro getType() {
        return ActionTypeAvro.INVERSE;
    }

    @Override
    public void handle() {
        log.info("Inverse scenario");
    }
}
