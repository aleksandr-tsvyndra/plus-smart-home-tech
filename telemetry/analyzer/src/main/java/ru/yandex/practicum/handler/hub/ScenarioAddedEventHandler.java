package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getHubEventType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro hubEvent) {
        var payload = (ScenarioAddedEventAvro) hubEvent.getPayload();
        checkSensors(payload.getConditions(), payload.getActions(), hubEvent.getHubId());
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), payload.getName());
        log.info("Удаляем старый сценарий, если он есть");
        scenario.ifPresent(oldScenario -> scenarioRepository.deleteByHubIdAndName(oldScenario.getHubId(),
                oldScenario.getName()));
        scenarioRepository.flush();
        var newScenario = HubEventMapper.mapToScenario(payload, hubEvent.getHubId());
        log.info("Сохраняем в БД следующий сценарий: {}", newScenario);
        scenarioRepository.save(newScenario);
    }

    private void checkSensors(
            List<ScenarioConditionAvro> conditions,
            List<DeviceActionAvro> actions,
            String hubId
    ) {
        List<String> conditionSensorIds = conditions.stream().map(ScenarioConditionAvro::getSensorId).toList();
        List<String> actionSensorIds = actions.stream().map(DeviceActionAvro::getSensorId).toList();
        if (!sensorRepository.existsAllByIdInAndHubId(conditionSensorIds, hubId)) {
            throw new IllegalArgumentException("Не найдены устройства, указанные в списке условий");
        }

        if (!sensorRepository.existsAllByIdInAndHubId(actionSensorIds, hubId)) {
            throw new IllegalArgumentException("Не найдены устройства, указанные в списке действий");
        }
    }
}
