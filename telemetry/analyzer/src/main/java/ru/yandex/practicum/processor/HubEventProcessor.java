package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaHubConsumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaHubConsumer consumer;
    private final HubEventService hubEventService;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try (consumer) {
            consumer.subscribeToTopics();
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll();
                if (!records.isEmpty()) {
                    int count = 0;
                    for (var record : records) {
                        log.info("HubConsumer получил из Kafka сообщение: {}", record);
                        hubEventService.handle(record.value());
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
            log.error("Ошибка во время обработки хаб-ивентов консьюмером KafkaHubConsumer", e);
        }
    }
}
