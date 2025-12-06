package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventService {
    private final Map<String, HubEventHandler> handlers;

    public HubEventService(Set<HubEventHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getHubEventType, Function.identity()));
    }

    public void handle(HubEventAvro hubEvent) {
        if (!handlers.containsKey(hubEvent.getPayload().getClass().getSimpleName())) {
            throw new IllegalArgumentException("Данный тип хаб-ивентов не поддерживается");
        }
        HubEventHandler handler = handlers.get(hubEvent.getPayload().getClass().getSimpleName());
        handler.handle(hubEvent);
    }
}
