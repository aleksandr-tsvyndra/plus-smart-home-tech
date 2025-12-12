package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Slf4j
@Component
public class DeviceAddedEventMapper extends BaseHubEventMapper<DeviceAddedEventAvro> {

    @Override
    protected DeviceAddedEventAvro mapToAvroPayload(HubEventProto event) {
        DeviceAddedEventProto deviceAddedEvent = event.getDeviceAdded();
        log.info("Маппим событие от хабов в объект типа {}", DeviceAddedEventAvro.class.getSimpleName());
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getType().name()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getHubEventType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}
