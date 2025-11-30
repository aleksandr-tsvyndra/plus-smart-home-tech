package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.IllegalTypeEventException;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.hub.HubEventMapper;
import ru.yandex.practicum.mapper.sensor.SensorEventMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final KafkaEventProducer producer;

    private final Map<SensorEventProto.PayloadCase, SensorEventMapper> sensorEventMappers;
    private final Map<HubEventProto.PayloadCase, HubEventMapper> hubEventMappers;

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
    public void processSensorEvent(SensorEventProto event) {
        if (!sensorEventMappers.containsKey(event.getPayloadCase())) {
            log.error("Не найден обработчик для типа события от датчиков: {}", event.getPayloadCase());
            throw new IllegalTypeEventException("Для данного типа события нет подходящего маппера");
        }
        SensorEventAvro avro = sensorEventMappers.get(event.getPayloadCase()).mapToAvro(event);
        log.info("Передаём событие от датчиков {} в Kafka-продюсер для подготовки к отправке", avro);
        producer.send(
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }

    @Override
    public void processHubEvent(HubEventProto event) {
        if (!hubEventMappers.containsKey(event.getPayloadCase())) {
            log.error("Не найден обработчик для типа события от хабов: {}", event.getPayloadCase());
            throw new IllegalTypeEventException("Для данного типа события нет подходящего маппера");
        }
        HubEventAvro avro = hubEventMappers.get(event.getPayloadCase()).mapToAvro(event);
        log.info("Передаём событие от хабов {} в Kafka-продюсер для подготовки к отправке", avro);
        producer.send(
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }
}
