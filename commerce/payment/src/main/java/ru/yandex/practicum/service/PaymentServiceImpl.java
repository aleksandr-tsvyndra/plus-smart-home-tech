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

import java.math.BigDecimal;
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

    private static final BigDecimal FEE = BigDecimal.valueOf(1.1);

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        if (orderDto.getTotalPrice().equals(BigDecimal.ZERO) || orderDto.getDeliveryPrice().equals(BigDecimal.ZERO)
                || orderDto.getProductPrice().equals(BigDecimal.ZERO)) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных для оплаты заказа");
        }
        Payment newPayment = buildNewPayment(orderDto);
        log.info("Сохраняем новую оплату в БД: {}", newPayment);
        return paymentMapper.toDto(paymentRepo.save(newPayment));
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        if (orderDto.getProductPrice().equals(BigDecimal.ZERO)
                || orderDto.getDeliveryPrice().equals(BigDecimal.ZERO)) {
            throw new NotEnoughInfoInOrderToCalculateException("Нехватает данных для расчёта полной стоимости заказа");
        }
        BigDecimal totalCost = orderDto.getProductPrice().multiply(FEE).add(orderDto.getDeliveryPrice());
        log.info("Полная стоимость заказа: {}", totalCost);
        return totalCost;
    }

    @Override
    public BigDecimal getProductCost(OrderDto orderDto) {
        Map<UUID,Integer> products = orderDto.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Список товаров не может быть null или пустым");
        }
        BigDecimal productsCost = BigDecimal.ZERO;
        for (var productId : products.keySet()) {
            try {
                ProductDto product = shoppingStore.findProductById(productId.toString());
                BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
                productsCost = productsCost.add(productPrice.multiply(BigDecimal.valueOf(products.get(productId))));
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
