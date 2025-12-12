package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.repository.SensorRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getHubEventType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro hubEvent) {
        var payload = (DeviceAddedEventAvro) hubEvent.getPayload();
        if (sensorRepository.existsByIdAndHubId(payload.getId(), hubEvent.getHubId())) {
            log.warn("Датчик с id={} и hubId={} уже добавлен", payload.getId(), hubEvent.getHubId());
        } else {
            log.info("Добавляем в БД новый датчик: {}", payload);
            var sensor = HubEventMapper.mapToSensor(payload.getId(), hubEvent.getHubId());
            sensorRepository.save(sensor);
        }
    }
}
