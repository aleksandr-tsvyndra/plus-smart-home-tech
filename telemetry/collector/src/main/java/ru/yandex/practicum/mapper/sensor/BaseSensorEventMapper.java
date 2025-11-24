package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Slf4j
public abstract class BaseSensorEventMapper<T extends SpecificRecordBase> implements SensorEventMapper {

    protected abstract T mapToAvroPayload(SensorEvent event);

    @Override
    public SensorEventAvro mapToAvro(SensorEvent event) {
        T payload = mapToAvroPayload(event);
        log.info("Маппим событие от датчиков в объект типа {}", SensorEventAvro.class.getSimpleName());
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
