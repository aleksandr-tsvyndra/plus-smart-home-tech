package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepo;
    private final DeliveryMapper mapper;

    private final OrderFeignClient orderFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;

    private static final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5.0);
    private static final BigDecimal DELIVERY_VOLUME_RATIO = BigDecimal.valueOf(0.2);
    private static final BigDecimal DELIVERY_WEIGHT_RATIO = BigDecimal.valueOf(0.3);
    private static final BigDecimal DELIVERY_FRAGILE_RATIO = BigDecimal.valueOf(0.2);
    private static final BigDecimal WAREHOUSE_ADDRESS_2_RATIO = BigDecimal.valueOf(2.0);
    private static final BigDecimal ADDRESS_RATIO = BigDecimal.valueOf(0.2);

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = mapper.toEntity(dto);
        var oldDelivery = deliveryRepo.findByOrderId(dto.getOrderId());
        if (oldDelivery.isPresent()) {
            log.info("В БД уже есть доставка для заказа с id={}: {}", dto.getOrderId(), oldDelivery.get());
            delivery.setDeliveryId(oldDelivery.get().getDeliveryId());
        } else {
            log.info("Добавляем в БД доставку для заказа с id={}", dto.getOrderId());
            delivery = deliveryRepo.save(delivery);
        }
        return mapper.toDto(delivery);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepo.save(delivery);
        orderFeignClient.delivery(delivery.getOrderId());
        log.info("Заказ с id={} успешно доставлен!", delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setState(DeliveryState.CANCELLED);
        deliveryRepo.save(delivery);
        orderFeignClient.deliveryFailed(delivery.getOrderId());
        log.warn("Заказ с id={} завершился с ошибкой доставки!", delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        orderFeignClient.assembly(delivery.getOrderId());
        warehouseFeignClient.shippedToDelivery(new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId));
        delivery.setState(DeliveryState.IN_PROGRESS);
        deliveryRepo.save(delivery);
        log.info("Товары успешно приняты в доставку!");
    }

    @Override
    public BigDecimal deliveryCost(OrderDto dto) {
        Delivery delivery = findDeliveryById(dto.getDeliveryId());
        BigDecimal total = BASE_DELIVERY_COST;
        log.info("Базовая цена доставки: {}", total);
        if (isSameAddress(delivery.getFromAddress(), "ADDRESS_1")) {
            total = total.add(BASE_DELIVERY_COST);
            log.info("Доставка содержит ADDRESS_1: складываем с базовой ценой: {}", total);
        } else if (isSameAddress(delivery.getFromAddress(), "ADDRESS_2")) {
            total = total.multiply(WAREHOUSE_ADDRESS_2_RATIO).add(BASE_DELIVERY_COST);
            log.info("Доставка содержит ADDRESS_2: умножаем на 2 и складываем с базовой ценой: {}", total);
        }
        if (dto.getFragile()) {
            total = total.multiply(DELIVERY_FRAGILE_RATIO).add(total);
            log.info("Цена доставки увеличена из-за признака хрупкости: {}", total);
        }
        total = BigDecimal.valueOf(dto.getDeliveryWeight()).multiply(DELIVERY_WEIGHT_RATIO).add(total);
        total = BigDecimal.valueOf(dto.getDeliveryVolume()).multiply(DELIVERY_VOLUME_RATIO).add(total);
        log.info("Добавили к цене доставки вес заказа и его объём: {}", total);
        if (!delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            total = total.multiply(ADDRESS_RATIO).add(total);
            log.info("Цена доставки увеличена — адрес доставки не совпадает с адресом склада: {}", total);
        }
        log.info("Итоговая цена доставки для заказа с id={} составляет: {}", dto.getOrderId(), total);
        return total;
    }

    private boolean isSameAddress(Address address, String target) {
        log.info("Проверяем адрес склада для расчёта цены доставки");
        if (address == null || target == null || target.isBlank()) return false;
        return Objects.equals(address.getCountry(), target)
                || Objects.equals(address.getCity(), target)
                || Objects.equals(address.getStreet(), target)
                || Objects.equals(address.getHouse(), target)
                || Objects.equals(address.getFlat(), target);
    }

    private Delivery findDeliveryById(UUID id) {
        log.info("Ищем доставку с id={} в БД...", id);
        return deliveryRepo.findById(id)
                .orElseThrow(() -> new NoDeliveryFoundException("В БД нет доставки с id=" + id));
    }
}
