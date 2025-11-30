package ru.yandex.practicum.mapper.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventMapper {

    SensorEventAvro mapToAvro(SensorEventProto event);

    SensorEventProto.PayloadCase getSensorEventType();

}
