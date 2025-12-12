package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Slf4j
@Component
public class ScenarioRemovedEventMapper extends BaseHubEventMapper<ScenarioRemovedEventAvro> {

    @Override
    protected ScenarioRemovedEventAvro mapToAvroPayload(HubEventProto event) {
        ScenarioRemovedEventProto scenarioRemovedEvent = event.getScenarioRemoved();
        log.info("Маппим событие от хабов в объект типа {}", ScenarioRemovedEventAvro.class.getSimpleName());
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getHubEventType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}
