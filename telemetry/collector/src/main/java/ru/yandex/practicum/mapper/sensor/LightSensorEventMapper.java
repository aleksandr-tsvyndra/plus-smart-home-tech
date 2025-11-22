package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Slf4j
@Component
public class LightSensorEventMapper extends BaseSensorEventMapper<LightSensorAvro> {

    @Override
    protected LightSensorAvro mapToAvroPayload(SensorEvent event) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) event;
        log.info("Маппим событие от датчиков в объект типа {}", LightSensorAvro.class.getSimpleName());
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
