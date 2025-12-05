package ru.yandex.practicum.client;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRouterClient {
    @GrpcClient("hub-router")
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void send(Scenario scenario) {
        Map<String, Action> actions = scenario.getActions();
        log.info("Подготавливаем и отправляем в сервис Hub router запросы с действиями");
        actions.keySet().stream()
                .map(sensorId -> mapToDeviceActionRequest(sensorId, actions.get(sensorId), scenario))
                .forEach(hubRouterClient::handleDeviceAction);
    }

    public void send(List<Scenario> scenarios) {
        scenarios.forEach(this::send);
    }

    private DeviceActionRequest mapToDeviceActionRequest(String sensorId, Action action, Scenario scenario) {
        log.info("Маппим объект типа Action в DeviceActionRequest");
        DeviceActionProto proto = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
        Timestamp ts = Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();
        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(proto)
                .setTimestamp(ts)
                .build();
    }
}
