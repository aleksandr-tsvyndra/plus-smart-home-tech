package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.order.NoOrderFoundException;
import ru.yandex.practicum.exception.shoppingCart.NotAuthorizedUserException;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final OrderMapper mapper;

    private final WarehouseFeignClient warehouse;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getClientOrders(String username, Pageable pageable) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("username не должен быть null или пустым");
        }
        Page<Order> orders = orderRepo.findAllByUsername(username, pageable);
        log.info("Заказы юзера {}, найденные в БД: {}", username, orders);
        return orders.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getClientOrderByPaymentId(UUID paymentId) {
        Order order = orderRepo.findByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с paymentId=" + paymentId + "не найден!"));
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(String username, CreateNewOrderRequest newOrder) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("username не должен быть null или пустым");
        }
        BookedProductsDto bookedProductsDto = warehouse.checkProductQuantityInWarehouse(newOrder.getShoppingCart());
        return null;
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest returnRequest) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.PAID);
        log.info("Значение поля state стало: {}. Сохраняем оплаченный заказ в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        log.info("Значение поля state стало: {}. Сохраняем заказ с ошибкой оплаты в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        log.info("Значение поля state стало: {}. Сохраняем доставленный заказ в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        log.info("Значение поля state стало: {}. Сохраняем заказ с ошибкой доставки в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        log.info("Значение поля state стало: {}. Сохраняем завершённый заказ в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        log.info("Значение поля state стало: {}. Сохраняем собранный заказ в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        log.info("Значение поля state стало: {}. Сохраняем заказ с ошибкой сборки в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
    }

    private Order findOrderById(UUID id) {
        log.info("Проверяем есть ли в системе заказ id={}...", id);
        return orderRepo.findById(id)
                .orElseThrow(() -> new NoOrderFoundException("В БД нет заказа с id=" + id));
    }
}
