package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Slf4j
@Component
public class ClimateSensorEventMapper extends BaseSensorEventMapper<ClimateSensorAvro> {

    @Override
    protected ClimateSensorAvro mapToAvroPayload(SensorEvent event) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) event;
        log.info("Маппим событие от датчиков в объект типа {}", ClimateSensorAvro.class.getSimpleName());
        return ClimateSensorAvro.newBuilder()
                .setTemperature(climateSensorEvent.getTemperatureC())
                .setHumidity(climateSensorEvent.getHumidity())
                .setCo2Level(climateSensorEvent.getCo2Level())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
