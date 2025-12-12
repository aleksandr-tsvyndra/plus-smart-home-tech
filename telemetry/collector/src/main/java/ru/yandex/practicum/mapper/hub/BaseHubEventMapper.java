package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;

import java.time.Instant;

@Slf4j
public abstract class BaseHubEventMapper<T extends SpecificRecordBase> implements HubEventMapper {

    protected abstract T mapToAvroPayload(HubEventProto event);

    @Override
    public HubEventAvro mapToAvro(HubEventProto event) {
        T payload = mapToAvroPayload(event);
        log.info("Маппим событие от хабов в объект типа {}", HubEvent.class.getSimpleName());
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }
}
