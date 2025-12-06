package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getHubEventType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        var payload = (ScenarioRemovedEventAvro) hubEvent.getPayload();
        log.info("Удаляем из БД следующий сценарий: {}", payload);
        scenarioRepository.deleteByHubIdAndName(hubEvent.getHubId(), payload.getName());
    }
}
