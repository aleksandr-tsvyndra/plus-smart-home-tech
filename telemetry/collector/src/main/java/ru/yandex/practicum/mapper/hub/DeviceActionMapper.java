package ru.yandex.practicum.mapper.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.util.List;

@Slf4j
@Component
public class DeviceActionMapper {

    public DeviceActionAvro mapToAvro(DeviceActionProto action) {
        log.info("Маппим описание скрипта в объект типа {}", DeviceActionAvro.class.getSimpleName());
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    public List<DeviceActionAvro> mapToAvro(List<DeviceActionProto> actions) {
        return actions.stream().map(this::mapToAvro).toList();
    }
}
