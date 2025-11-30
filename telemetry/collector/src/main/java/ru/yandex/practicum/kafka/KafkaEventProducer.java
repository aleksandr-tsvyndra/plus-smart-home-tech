package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer implements AutoCloseable, DisposableBean {
    private final Producer<String, SpecificRecordBase> kafkaProducer;

    @Value(value = "${collector.kafka.producer.topics.sensors-events}")
    private String sensorsTopic;
    @Value(value = "${collector.kafka.producer.topics.hub-events}")
    private String hubsTopic;

    public void send(Long timestamp, String hubId, SpecificRecordBase value) {
        try {
            String topic;
            if (value instanceof SensorEventAvro) {
                topic = sensorsTopic;
            } else {
                topic = hubsTopic;
            }
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                    topic, null, timestamp, hubId, value
            );
            log.info("Отправляем в топик {} запись: {}", topic, record);
            kafkaProducer.send(record);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения!", e);
        }
    }

    @Override
    public void close() {
        try {
            kafkaProducer.flush();
            log.info("Закрываем KafkaEventProducer...");
            kafkaProducer.close(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("Ошибка при попытке закрыть KafkaEventProducer!", e);
            throw e;
        }
    }

    @Override
    public void destroy() {
        close();
    }
}
