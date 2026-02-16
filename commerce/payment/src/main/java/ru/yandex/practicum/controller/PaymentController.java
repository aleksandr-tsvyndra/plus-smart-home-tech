package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.PaymentApi;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
    private final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Запрос для формирования оплаты заказа с id={}", orderDto.getOrderId());
        return paymentService.createPayment(orderDto);
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        log.info("Запрос на расчёт полной стоимости заказа с id={}", orderDto.getOrderId());
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public Double getProductCost(OrderDto orderDto) {
        log.info("Запрос на расчёт стоимости товаров в заказе с id={}", orderDto.getOrderId());
        return paymentService.getProductCost(orderDto);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("Запрос на вызов метода для эмуляции успешной оплаты с id={}", paymentId);
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Запрос на вызов метода для эмуляции отказа в оплате с id={}", paymentId);
        paymentService.paymentFailed(paymentId);
    }
}
