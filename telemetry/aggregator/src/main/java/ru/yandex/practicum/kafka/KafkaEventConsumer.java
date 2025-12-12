package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventConsumer implements AutoCloseable {
    private final Consumer<String, SpecificRecordBase> consumer;

    @Value("${aggregator.kafka.topics.sensors-events}")
    private String sensorsEventsTopic;

    public void subscribeToTopics() {
        this.consumer.subscribe(List.of(sensorsEventsTopic));
    }

    public ConsumerRecords<String, SpecificRecordBase> poll() {
        return this.consumer.poll(Duration.ofMillis(1000));
    }

    public void commitAsync() {
        this.consumer.commitAsync();
    }

    public void wakeup() {
        this.consumer.wakeup();
    }

    @Override
    public void close() throws Exception {
        try {
            this.consumer.commitSync();
            log.info("Закрываем KafkaEventConsumer...");
            this.consumer.close();
        } catch (Exception e) {
            log.error("Ошибка при попытке закрыть KafkaEventConsumer!", e);
            throw e;
        }
    }
}
