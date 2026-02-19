package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.PaymentState;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.exception.payment.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.payment.PaymentNotFoundException;
import ru.yandex.practicum.exception.shoppingStore.ProductNotFoundException;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.feignClient.ShoppingStoreFeignClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;

    private final ShoppingStoreFeignClient shoppingStore;
    private final OrderFeignClient order;

    private static final Double FEE = 1.1;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == 0 || orderDto.getDeliveryPrice() == 0 || orderDto.getProductPrice() == 0) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для оплаты заказа");
        }
        Payment newPayment = buildNewPayment(orderDto);
        log.info("Сохраняем новую оплату в БД: {}", newPayment);
        return paymentMapper.toDto(paymentRepo.save(newPayment));
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        if (orderDto.getProductPrice() == 0 || orderDto.getDeliveryPrice() == 0) {
            throw new NotEnoughInfoInOrderToCalculateException("Нехватает данных для расчёта полной стоимости заказа");
        }
        Double totalCost = orderDto.getProductPrice() * FEE + orderDto.getDeliveryPrice();
        log.info("Полная стоимость заказа: {}", totalCost);
        return totalCost;
    }

    @Override
    public Double getProductCost(OrderDto orderDto) {
        Map<UUID,Integer> products = orderDto.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Список товаров не может быть null или пустым");
        }
        double productsCost = 0.0;
        for (var productId : products.keySet()) {
            try {
                ProductDto product = shoppingStore.findProductById(productId.toString());
                productsCost += products.get(productId) * product.getPrice();
            } catch (ProductNotFoundException e) {
                log.warn("Товар с id={} не найден. Никак не реагируем.", productId);
            }
        }
        log.info("Стоимость всех товаров в заказе: {}", productsCost);
        return productsCost;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Оплата с id=" + paymentId + " не найдена!"));
        payment.setState(PaymentState.SUCCESS);
        paymentRepo.save(payment);
        OrderDto orderDto = order.getClientOrderByPaymentId(paymentId);
        order.payment(orderDto.getOrderId());
        log.info("Заказ с id={} и paymentId={} успешно оплачен!", orderDto.getOrderId(), paymentId);
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Оплата с id=" + paymentId + " не найдена!"));
        payment.setState(PaymentState.FAILED);
        paymentRepo.save(payment);
        OrderDto orderDto = order.getClientOrderByPaymentId(paymentId);
        order.paymentFailed(orderDto.getOrderId());
        log.info("Оплата заказа с id={} и paymentId={} завершилась с ошибкой!", orderDto.getOrderId(), paymentId);
    }

    private Payment buildNewPayment(OrderDto orderDto) {
        Payment payment = new Payment();
        payment.setTotalProduct(orderDto.getProductPrice());
        payment.setDeliveryTotal(orderDto.getDeliveryPrice());
        payment.setTotalPayment(orderDto.getTotalPrice());
        return payment;
    }
}
