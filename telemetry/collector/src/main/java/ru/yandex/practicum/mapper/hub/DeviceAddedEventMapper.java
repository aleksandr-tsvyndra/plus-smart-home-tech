package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

@Slf4j
@Component
public class DeviceAddedEventMapper extends BaseHubEventMapper<DeviceAddedEventAvro> {

    @Override
    protected DeviceAddedEventAvro mapToAvroPayload(HubEvent event) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
        log.info("Маппим событие от хабов в объект типа {}", DeviceAddedEventAvro.class.getSimpleName());
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_ADDED;
    }
}
