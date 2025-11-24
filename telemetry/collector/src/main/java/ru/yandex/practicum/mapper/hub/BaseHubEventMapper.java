package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;

@Slf4j
public abstract class BaseHubEventMapper<T extends SpecificRecordBase> implements HubEventMapper {

    protected abstract T mapToAvroPayload(HubEvent event);

    @Override
    public HubEventAvro mapToAvro(HubEvent event) {
        T payload = mapToAvroPayload(event);
        log.info("Маппим событие от хабов в объект типа {}", HubEvent.class.getSimpleName());
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
