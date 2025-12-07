package ru.yandex.practicum.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class LightSensorHandler implements SensorHandler {

    @Override
    public String getType() {
        return LightSensorAvro.class.getSimpleName();
    }

    @Override
    public Integer handleValue(SensorStateAvro stateAvro, ConditionType type) {
        var sensorAvro = (LightSensorAvro) stateAvro.getData();
        return type.equals(ConditionType.LUMINOSITY) ? sensorAvro.getLuminosity() : null;
    }
}
