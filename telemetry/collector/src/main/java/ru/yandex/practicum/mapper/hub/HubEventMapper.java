package ru.yandex.practicum.mapper.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

public interface HubEventMapper {

    HubEventAvro mapToAvro(HubEvent event);

    HubEventType getHubEventType();

}
