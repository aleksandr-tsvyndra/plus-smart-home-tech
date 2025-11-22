package ru.yandex.practicum.mapper.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.hub.DeviceAction;

import java.util.List;

@Component
public class DeviceActionMapper {
    private static final Logger log = LoggerFactory.getLogger(DeviceActionMapper.class);

    public DeviceActionAvro mapToAvro(DeviceAction action) {
        log.info("Маппим описание скрипта в объект типа {}", DeviceActionAvro.class.getSimpleName());
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    public List<DeviceActionAvro> mapToAvro(List<DeviceAction> actions) {
        return actions.stream().map(this::mapToAvro).toList();
    }
}
