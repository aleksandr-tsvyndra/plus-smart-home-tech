package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Slf4j
@Component
public class MotionSensorEventMapper extends BaseSensorEventMapper<MotionSensorAvro> {

    @Override
    protected MotionSensorAvro mapToAvroPayload(SensorEventProto event) {
        MotionSensorProto motionSensorEvent = event.getMotionSensor();
        log.info("Маппим событие от датчиков в объект типа {}", MotionSensorAvro.class.getSimpleName());
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getSensorEventType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }
}
