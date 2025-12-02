package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.KafkaSnapshotProducer;

public interface AggregatorService {
    void aggregateSnapshot(KafkaSnapshotProducer producer, SpecificRecordBase record);
}
