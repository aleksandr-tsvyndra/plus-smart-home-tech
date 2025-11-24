package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Slf4j
@Component
public class TemperatureSensorEventMapper extends BaseSensorEventMapper<TemperatureSensorAvro> {

    @Override
    protected TemperatureSensorAvro mapToAvroPayload(SensorEvent event) {
        TemperatureSensorEvent sensorEvent = (TemperatureSensorEvent) event;
        log.info("Маппим событие от датчиков в объект типа {}", TemperatureSensorAvro.class.getSimpleName());
        return TemperatureSensorAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setTemperatureC(sensorEvent.getTemperatureC())
                .setTemperatureF(sensorEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
