package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface EventService {

    void processSensorEvent(SensorEventProto event);

    void processHubEvent(HubEventProto event);

}
