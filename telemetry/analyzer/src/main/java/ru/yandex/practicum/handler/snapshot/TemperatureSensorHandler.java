package ru.yandex.practicum.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class TemperatureSensorHandler implements SensorHandler {
    @Override
    public String getType() {
        return TemperatureSensorAvro.class.getSimpleName();
    }

    @Override
    public Integer handleValue(SensorStateAvro stateAvro, ConditionType type) {
        TemperatureSensorAvro sensorAvro = (TemperatureSensorAvro) stateAvro.getData();
        return type.equals(ConditionType.TEMPERATURE) ? sensorAvro.getTemperatureC() : null;
    }
}
