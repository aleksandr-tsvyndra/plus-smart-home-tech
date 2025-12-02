package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSnapshotProducer implements AutoCloseable {
    private final Producer<String, SpecificRecordBase> producer;

    @Value("${aggregator.kafka.topics.snapshots-events}")
    private String snapshotTopic;

    public void send(SensorsSnapshotAvro snapshot) {
        try {
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                    snapshotTopic, null, snapshot.getTimestamp().toEpochMilli(), snapshot.getHubId(), snapshot
            );
            log.info("Отправляем в топик {} запись: {}", snapshotTopic, record);
            this.producer.send(record);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения!", e);
        }
    }

    @Override
    public void close() throws Exception {
        try {
            this.producer.flush();
            log.info("Закрываем KafkaSnapshotProducer...");
            this.producer.close(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("Ошибка при попытке закрыть KafkaSnapshotProducer!", e);
            throw e;
        }
    }
}
