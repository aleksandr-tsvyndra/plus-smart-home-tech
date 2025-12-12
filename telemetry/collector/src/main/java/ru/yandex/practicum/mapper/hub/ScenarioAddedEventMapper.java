package ru.yandex.practicum.mapper.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventMapper extends BaseHubEventMapper<ScenarioAddedEventAvro> {
    private final ScenarioConditionMapper scenarioConditionMapper;
    private final DeviceActionMapper deviceActionMapper;

    @Override
    protected ScenarioAddedEventAvro mapToAvroPayload(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        log.info("Маппим событие от хабов в объект типа {}", ScenarioAddedEventAvro.class.getSimpleName());
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(scenarioConditionMapper.mapToAvro(scenarioAddedEvent.getConditionList()))
                .setActions(deviceActionMapper.mapToAvro(scenarioAddedEvent.getActionList()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getHubEventType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
