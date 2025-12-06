package ru.yandex.practicum.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class SwitchSensorHandler implements SensorHandler {

    @Override
    public String getType() {
        return SwitchSensorAvro.class.getSimpleName();
    }

    @Override
    public Integer handleValue(SensorStateAvro stateAvro, ConditionType type) {
        SwitchSensorAvro sensorAvro = (SwitchSensorAvro) stateAvro.getData();
        return type.equals(ConditionType.SWITCH) ? sensorAvro.getState() ? 1 : 0 : null;
    }
}
