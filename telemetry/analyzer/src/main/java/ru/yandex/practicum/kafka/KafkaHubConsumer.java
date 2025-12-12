package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHubConsumer implements AutoCloseable {
    private final Consumer<String, HubEventAvro> consumer;

    @Value("${analyzer.kafka.topics.hubs-events}")
    private String hubEventsTopic;

    public void subscribeToTopics() {
        this.consumer.subscribe(List.of(hubEventsTopic));
    }

    public ConsumerRecords<String, HubEventAvro> poll() {
        return this.consumer.poll(Duration.ofMillis(1000));
    }

    public void commitAsync() {
        this.consumer.commitAsync();
    }

    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> var1) {
        this.consumer.commitAsync(var1, (offsets, exception) -> {
            if (exception != null) {
                log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
            }
        });
    }

    public void wakeup() {
        this.consumer.wakeup();
    }

    @Override
    public void close() throws Exception {
        try {
            this.consumer.commitSync();
            log.info("Закрываем KafkaHubConsumer...");
            this.consumer.close();
        } catch (Exception e) {
            log.error("Ошибка при попытке закрыть KafkaHubConsumer!", e);
            throw e;
        }
    }
}
