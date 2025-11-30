package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Slf4j
@Component
public class LightSensorEventMapper extends BaseSensorEventMapper<LightSensorAvro> {

    @Override
    protected LightSensorAvro mapToAvroPayload(SensorEventProto event) {
        LightSensorProto lightSensorEvent = event.getLightSensor();
        log.info("Маппим событие от датчиков в объект типа {}", LightSensorAvro.class.getSimpleName());
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getSensorEventType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }
}
