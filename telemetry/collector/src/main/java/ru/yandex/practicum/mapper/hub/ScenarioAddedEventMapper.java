package ru.yandex.practicum.mapper.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventMapper extends BaseHubEventMapper<ScenarioAddedEventAvro> {
    private final ScenarioConditionMapper scenarioConditionMapper;
    private final DeviceActionMapper deviceActionMapper;

    @Override
    protected ScenarioAddedEventAvro mapToAvroPayload(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        log.info("Маппим событие от хабов в объект типа {}", ScenarioAddedEventAvro.class.getSimpleName());
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(scenarioConditionMapper.mapToAvro(scenarioAddedEvent.getConditions()))
                .setActions(deviceActionMapper.mapToAvro(scenarioAddedEvent.getActions()))
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
