package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaSnapshotConsumer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaSnapshotConsumer consumer;
    private final SnapshotService snapshotService;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try (consumer) {
            consumer.subscribeToTopics();
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll();
                if (!records.isEmpty()) {
                    int count = 0;
                    for (var record : records) {
                        log.info("SnapshotConsumer получил из Kafka сообщение: {}", record);
                        snapshotService.handle(record.value());
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1));
                        if (count % 10 == 0) {
                            consumer.commitAsync(currentOffsets);
                        }
                        count++;
                    }
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов консьюмером KafkaSnapshotConsumer", e);
        }
    }
}
