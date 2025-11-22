package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Slf4j
@Component
public class MotionSensorEventMapper extends BaseSensorEventMapper<MotionSensorAvro> {

    @Override
    protected MotionSensorAvro mapToAvroPayload(SensorEvent event) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) event;
        log.info("Маппим событие от датчиков в объект типа {}", MotionSensorAvro.class.getSimpleName());
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
