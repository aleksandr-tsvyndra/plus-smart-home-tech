package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.HubRouterClient;
import ru.yandex.practicum.handler.snapshot.SensorHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SnapshotService {
    private final HubRouterClient hubRouterClient;
    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorHandler> handlers;

    public SnapshotService(
            HubRouterClient hubRouterClient,
            ScenarioRepository scenarioRepository,
            Set<SensorHandler> handlers
    ) {
        this.hubRouterClient = hubRouterClient;
        this.scenarioRepository = scenarioRepository;
        this.handlers = handlers.stream().collect(Collectors.toMap(SensorHandler::getType, Function.identity()));
    }

    public void handle(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(snapshot.getHubId());
        if (scenarios.isEmpty()) {
            throw new IllegalArgumentException("У хаба с id " + snapshot.getHubId() + " нет сценариев");
        }
        log.info("Получаем сценарии, которые можно выполнить для хаба с id {}", snapshot.getHubId());
        List<Scenario> toDoScenarios = scenarios.stream()
                .filter(scenario -> validateConditions(scenario, snapshot))
                .toList();
        hubRouterClient.send(toDoScenarios);
    }

    private Boolean validateConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        Map<String, Condition> conditions = scenario.getConditions();
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorState();
        if (conditions.isEmpty() || sensorStates.isEmpty()) {
            return false;
        }
        return conditions.keySet().stream()
                .allMatch(sensorId -> validateConditions(conditions.get(sensorId), sensorStates.get(sensorId)));
    }

    private Boolean validateConditions(Condition condition, SensorStateAvro sensorState) {
        if (sensorState == null) {
            return false;
        }
        if (!handlers.containsKey(sensorState.getData().getClass().getSimpleName())) {
            throw new IllegalArgumentException("Данный тип датчиков не поддерживается");
        }
        SensorHandler handler = handlers.get(sensorState.getData().getClass().getSimpleName());
        Integer value = handler.handleValue(sensorState, condition.getType());
        return getConditionOperation(condition, value);
    }

    private Boolean getConditionOperation(Condition condition, Integer value) {
        if (value == null) {
            return false;
        }
        return switch (condition.getOperation()) {
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
        };
    }
}
