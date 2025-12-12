package ru.yandex.practicum.handler.snapshot;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

public interface SensorHandler {

    String getType();

    Integer handleValue(SensorStateAvro stateAvro, ConditionType type);

}
