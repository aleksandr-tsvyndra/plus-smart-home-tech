package ru.yandex.practicum.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class MotionSensorHandler implements SensorHandler {

    @Override
    public String getType() {
        return MotionSensorAvro.class.getSimpleName();
    }

    @Override
    public Integer handleValue(SensorStateAvro stateAvro, ConditionType type) {
        var sensorAvro = (MotionSensorAvro) stateAvro.getData();
        if (type.equals(ConditionType.MOTION)) {
            return sensorAvro.getMotion() ? 1 : 0;
        } else {
            return null;
        }
    }
}
