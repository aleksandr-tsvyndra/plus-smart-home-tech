package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Slf4j
@Component
public class ClimateSensorEventMapper extends BaseSensorEventMapper<ClimateSensorAvro> {

    @Override
    protected ClimateSensorAvro mapToAvroPayload(SensorEventProto event) {
        ClimateSensorProto climateSensorEvent = event.getClimateSensor();
        log.info("Маппим событие от датчиков в объект типа {}", ClimateSensorAvro.class.getSimpleName());
        return ClimateSensorAvro.newBuilder()
                .setTemperature(climateSensorEvent.getTemperatureC())
                .setHumidity(climateSensorEvent.getHumidity())
                .setCo2Level(climateSensorEvent.getCo2Level())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getSensorEventType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}
