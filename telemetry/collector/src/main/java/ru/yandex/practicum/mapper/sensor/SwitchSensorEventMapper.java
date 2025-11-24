package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Slf4j
@Component
public class SwitchSensorEventMapper extends BaseSensorEventMapper<SwitchSensorAvro> {

    @Override
    protected SwitchSensorAvro mapToAvroPayload(SensorEvent event) {
        SwitchSensorEvent sensorEvent = (SwitchSensorEvent) event;
        log.info("Маппим событие от датчиков в объект типа {}", SwitchSensorAvro.class.getSimpleName());
        return SwitchSensorAvro.newBuilder()
                .setState(sensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
