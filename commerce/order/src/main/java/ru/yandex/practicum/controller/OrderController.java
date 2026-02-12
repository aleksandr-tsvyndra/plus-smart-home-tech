package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.OrderApi;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {
    private final OrderService orderService;

    @Override
    public Page<OrderDto> getClientOrders(String username, Pageable pageable) {
        log.info("Запрос на получение заказов юзера с username: {}", username);
        return orderService.getClientOrders(username, pageable);
    }

    @Override
    public OrderDto createNewOrder(String username, CreateNewOrderRequest newOrderRequest) {
        log.info("Запрос на создание нового заказа в системе");
        return orderService.createNewOrder(username, newOrderRequest);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest returnRequest) {
        log.info("Запрос на возврат заказа c id={}", returnRequest.getOrderId());
        return orderService.productReturn(returnRequest);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Запрос на оплату заказа с id={}", orderId);
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Запрос на заказ с id={}, при оплате которого произошла ошибка", orderId);
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Запрос на доставку заказа с id={}", orderId);
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Запрос на заказ с id={}, при доставке которого произошла ошибка", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Запрос на обработку завершённого заказа с id={}", orderId);
        return orderService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Запрос на расчёт общей стоимости заказа с id={}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Запрос на расчёт доставки заказа с id={}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Запрос на сборку заказа с id={}", orderId);
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Запрос на заказ с id={}, сборка которого произошла с ошибкой", orderId);
        return orderService.assemblyFailed(orderId);
    }
}
