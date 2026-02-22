package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto dto);

    void deliverySuccessful(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    BigDecimal deliveryCost(OrderDto dto);

}
