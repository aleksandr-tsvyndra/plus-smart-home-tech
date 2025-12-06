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
        MotionSensorAvro sensorAvro = (MotionSensorAvro) stateAvro.getData();
        return type.equals(ConditionType.MOTION) ? sensorAvro.getMotion() ? 1 : 0 : null;
    }
}
