package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.DeliveryApi;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto dto) {
        log.info("Запрос на создание новой доставки в БД для заказа с id={}", dto.getOrderId());
        return deliveryService.planDelivery(dto);
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        log.info("Запрос эмуляции успешной доставки товара с deliveryId={}", deliveryId);
        deliveryService.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        log.info("Запрос эмуляции неудачной доставки товара с deliveryId={}", deliveryId);
        deliveryService.deliveryFailed(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        log.info("Запрос эмуляции получения товара в доставку с deliveryId={}", deliveryId);
        deliveryService.deliveryPicked(deliveryId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto dto) {
        log.info("Запрос на расчёт полной стоимости доставки заказа с id={}", dto.getOrderId());
        return deliveryService.deliveryCost(dto);
    }
}
