package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaEventConsumer;
import ru.yandex.practicum.kafka.KafkaSnapshotProducer;
import ru.yandex.practicum.service.AggregatorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaSnapshotProducer producer;
    private final KafkaEventConsumer consumer;

    private final AggregatorService aggregatorService;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try (producer; consumer) {
            consumer.subscribeToTopics();
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll();
                for (var record : records) {
                    log.info("Вызываем метод сервиса aggregateSnapshot для агрегации сообщения");
                    aggregatorService.aggregateSnapshot(producer, record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        }
    }
}
