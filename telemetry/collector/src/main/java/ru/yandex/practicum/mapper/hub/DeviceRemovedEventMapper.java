package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Slf4j
@Component
public class DeviceRemovedEventMapper extends BaseHubEventMapper<DeviceRemovedEventAvro> {

    @Override
    protected DeviceRemovedEventAvro mapToAvroPayload(HubEventProto event) {
        DeviceRemovedEventProto deviceRemovedEvent = event.getDeviceRemoved();
        log.info("Маппим событие от хабов в объект типа {}", DeviceRemovedEventAvro.class.getSimpleName());
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getHubEventType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
}
