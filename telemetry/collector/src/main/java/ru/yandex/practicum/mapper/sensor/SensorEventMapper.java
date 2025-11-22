package ru.yandex.practicum.mapper.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

public interface SensorEventMapper {

    SensorEventAvro mapToAvro(SensorEvent event);

    SensorEventType getSensorEventType();

}
