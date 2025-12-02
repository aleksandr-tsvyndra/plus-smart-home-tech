package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.KafkaSnapshotProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AggregatorServiceImpl implements AggregatorService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public void aggregateSnapshot(KafkaSnapshotProducer producer, SpecificRecordBase record) {
        Optional<SensorsSnapshotAvro> snapshot = updateState((SensorEventAvro) record);
        snapshot.ifPresent(producer::send);
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot;
        if (!snapshots.containsKey(event.getHubId())) {
            snapshot = buildSnapshot(event);
            snapshots.put(snapshot.getHubId(), snapshot);
        } else {
            snapshot = snapshots.get(event.getHubId());
            if (snapshot.getSensorState().containsKey(event.getId())) {
                SensorStateAvro oldState = snapshot.getSensorState().get(event.getId());
                if (oldState.getTimestamp().isAfter(event.getTimestamp())
                        || oldState.getData().equals(event.getPayload())) {
                    return Optional.empty();
                }
            }
            snapshot.getSensorState().put(event.getId(), buildSensorState(event));
            snapshot.setTimestamp(event.getTimestamp());
        }
        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro buildSnapshot(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorState(new HashMap<>())
                .build();
        snapshot.getSensorState().put(event.getId(), buildSensorState(event));
        return snapshot;
    }

    private SensorStateAvro buildSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
