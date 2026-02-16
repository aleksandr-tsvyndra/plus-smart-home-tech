package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    Double getTotalCost(OrderDto orderDto);

    Double getProductCost(OrderDto orderDto);

    void paymentSuccess(UUID paymentId);

    void paymentFailed(UUID paymentId);

}
