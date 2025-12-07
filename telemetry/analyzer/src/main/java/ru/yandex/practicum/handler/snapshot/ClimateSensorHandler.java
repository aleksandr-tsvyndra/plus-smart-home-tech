package ru.yandex.practicum.handler.snapshot;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.enums.ConditionType;

@Component
public class ClimateSensorHandler implements SensorHandler {

    @Override
    public String getType() {
        return ClimateSensorAvro.class.getSimpleName();
    }

    @Override
    public Integer handleValue(SensorStateAvro stateAvro, ConditionType type) {
        var sensorAvro = (ClimateSensorAvro) stateAvro.getData();
        return switch (type) {
            case TEMPERATURE -> sensorAvro.getTemperature();
            case HUMIDITY -> sensorAvro.getHumidity();
            case CO2LEVEL -> sensorAvro.getCo2Level();
            default -> null;
        };
    }
}
