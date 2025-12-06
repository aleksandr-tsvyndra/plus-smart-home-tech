package ru.yandex.practicum.mapper;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.model.enums.ActionType;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;

import java.util.Map;
import java.util.stream.Collectors;

public class HubEventMapper {

    private HubEventMapper() {}

    public static Scenario mapToScenario(ScenarioAddedEventAvro avro, String hubId) {
        Scenario scenario = Scenario.builder().hubId(hubId).name(avro.getName()).build();

        Map<String, Condition> conditionMap = avro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, HubEventMapper::mapToCondition));
        Map<String, Action> actionMap = avro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, HubEventMapper::mapToAction));

        scenario.setConditions(conditionMap);
        scenario.setActions(actionMap);
        return scenario;
    }

    public static Sensor mapToSensor(String id, String hubId) {
        return Sensor.builder()
                .id(id)
                .hubId(hubId)
                .build();
    }

    public static Action mapToAction(DeviceActionAvro actionAvro) {
        return Action.builder()
                .type(ActionType.valueOf(actionAvro.getType().name()))
                .value(actionAvro.getValue())
                .build();
    }

    public static Condition mapToCondition(ScenarioConditionAvro conditionAvro) {
        return Condition.builder()
                .type(ConditionType.valueOf(conditionAvro.getType().name()))
                .operation(ConditionOperation.valueOf(conditionAvro.getOperation().name()))
                .value(getValue(conditionAvro.getValue()))
                .build();
    }

    private static Integer getValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }
}
