package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Slf4j
@Component
public class DeviceRemovedEventMapper extends BaseHubEventMapper<DeviceRemovedEventAvro> {

    @Override
    protected DeviceRemovedEventAvro mapToAvroPayload(HubEvent event) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
        log.info("Маппим событие от хабов в объект типа {}", DeviceRemovedEventAvro.class.getSimpleName());
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
