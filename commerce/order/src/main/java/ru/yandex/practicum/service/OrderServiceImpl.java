package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.order.NoOrderFoundException;
import ru.yandex.practicum.exception.shoppingCart.NotAuthorizedUserException;
import ru.yandex.practicum.feignClient.DeliveryFeignClient;
import ru.yandex.practicum.feignClient.PaymentFeignClient;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final OrderMapper mapper;

    private final WarehouseFeignClient warehouse;
    private final PaymentFeignClient payment;
    private final DeliveryFeignClient delivery;

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
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("username не должен быть null или пустым");
        }
        Order order = orderRepo.save(buildOrder(username, request));
        UUID orderId = order.getOrderId();

        BookedProductsDto bookedProducts = warehouse.assemblyProductsForOrder(
                buildAssemblyProductsForOrderRequest(orderId, request.getShoppingCart().getProducts()));

        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());
        order.setProductPrice(payment.getProductCost(mapper.toDto(order)));

        AddressDto warehouseAddrs = warehouse.getWarehouseAddress();
        AddressDto deliveryAddrs = request.getDeliveryAddress();

        DeliveryDto newDelivery = delivery.planDelivery(buildDeliveryDto(warehouseAddrs, deliveryAddrs, orderId));
        order.setDeliveryId(newDelivery.getDeliveryId());
        order.setDeliveryPrice(delivery.deliveryCost(mapper.toDto(order)));

        order.setTotalPrice(payment.getTotalCost(mapper.toDto(order)));

        PaymentDto orderCost = payment.createPayment(mapper.toDto(order));
        order.setPaymentId(orderCost.getPaymentId());

        orderRepo.save(order);
        payment.paymentSuccess(orderCost.getPaymentId());

        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest returnRequest) {
        Order order = findOrderById(returnRequest.getOrderId());
        warehouse.acceptReturn(returnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        log.info("Значение поля state стало: {}. Сохраняем заказ с возвратом товара в БД...", order.getState());
        return mapper.toDto(orderRepo.save(order));
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
        log.warn("Значение поля state стало: {}. Сохраняем заказ с ошибкой доставки в БД...", order.getState());
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
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setTotalPrice(payment.getTotalCost(mapper.toDto(order)));
        return mapper.toDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setDeliveryPrice(delivery.deliveryCost(mapper.toDto(order)));
        return mapper.toDto(orderRepo.save(order));
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

    private Order buildOrder(String username, CreateNewOrderRequest request) {
        return Order.builder()
                .shoppingCartId(UUID.fromString(request.getShoppingCart().getShoppingCartId()))
                .products(request.getShoppingCart().getProducts())
                .username(username)
                .build();
    }

    private AssemblyProductsForOrderRequest buildAssemblyProductsForOrderRequest(UUID orderId,
                                                                                 Map<UUID, Integer> products) {
        return AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(products)
                .build();
    }

    private DeliveryDto buildDeliveryDto(AddressDto from, AddressDto to, UUID orderId) {
        return DeliveryDto.builder()
                .fromAddress(from)
                .toAddress(to)
                .orderId(orderId)
                .build();
    }
}
