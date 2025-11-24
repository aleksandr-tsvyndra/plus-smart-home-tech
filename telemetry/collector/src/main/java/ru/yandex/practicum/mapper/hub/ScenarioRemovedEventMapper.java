package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Slf4j
@Component
public class ScenarioRemovedEventMapper extends BaseHubEventMapper<ScenarioRemovedEventAvro> {

    @Override
    protected ScenarioRemovedEventAvro mapToAvroPayload(HubEvent event) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
        log.info("Маппим событие от хабов в объект типа {}", ScenarioRemovedEventAvro.class.getSimpleName());
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
