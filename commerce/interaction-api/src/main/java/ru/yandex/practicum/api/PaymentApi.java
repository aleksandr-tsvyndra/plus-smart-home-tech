package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentApi {

    @PostMapping("/api/v1/payment")
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    Double getTotalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/productCost")
    Double getProductCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
