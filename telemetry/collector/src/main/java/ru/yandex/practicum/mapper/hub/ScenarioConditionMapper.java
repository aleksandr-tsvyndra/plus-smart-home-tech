package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Slf4j
@Component
public class ScenarioConditionMapper {

    public ScenarioConditionAvro mapToAvro(ScenarioConditionProto c) {
        log.info("Маппим условие активации скрипта в объект типа {}", ScenarioConditionAvro.class.getSimpleName());
        var value = c.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE ? c.getIntValue() : c.getBoolValue();
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(c.getSensorId())
                .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                .setValue(value)
                .build();
    }

    public List<ScenarioConditionAvro> mapToAvro(List<ScenarioConditionProto> conditions) {
        return conditions.stream().map(this::mapToAvro).toList();
    }
}
