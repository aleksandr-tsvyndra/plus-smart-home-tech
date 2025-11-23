package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.hub.HubEventMapper;
import ru.yandex.practicum.mapper.sensor.SensorEventMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final KafkaEventProducer producer;

    private final Map<SensorEventType, SensorEventMapper> sensorEventMappers;
    private final Map<HubEventType, HubEventMapper> hubEventMappers;

    public EventServiceImpl(
            KafkaEventProducer producer,
            Set<SensorEventMapper> sensorEventMappersSet,
            Set<HubEventMapper> hubEventMappersSet
    ) {
        this.producer = producer;
        this.sensorEventMappers = sensorEventMappersSet.stream()
                .collect(Collectors.toMap(SensorEventMapper::getSensorEventType, Function.identity()));
        this.hubEventMappers = hubEventMappersSet.stream()
                .collect(Collectors.toMap(HubEventMapper::getHubEventType, Function.identity()));
    }

    @Override
    public void processSensorEvent(SensorEvent event) {
        if (!sensorEventMappers.containsKey(event.getType())) {
            log.error("Не найден обработчик для типа события от датчиков: {}", event.getType());
            throw new IllegalArgumentException("Для данного типа нет подходящего маппера");
        }
        SensorEventAvro avro = sensorEventMappers.get(event.getType()).mapToAvro(event);
        log.info("Передаём событие от датчиков {} в Kafka-продюсер для подготовки к отправке", avro);
        producer.send(
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }

    @Override
    public void processHubEvent(HubEvent event) {
        if (!hubEventMappers.containsKey(event.getType())) {
            log.error("Не найден обработчик для типа события от хабов: {}", event.getType());
            throw new IllegalArgumentException("Для данного типа нет подходящего маппера");
        }
        HubEventAvro avro = hubEventMappers.get(event.getType()).mapToAvro(event);
        log.info("Передаём событие от хабов {} в Kafka-продюсер для подготовки к отправке", avro);
        producer.send(
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }
}
