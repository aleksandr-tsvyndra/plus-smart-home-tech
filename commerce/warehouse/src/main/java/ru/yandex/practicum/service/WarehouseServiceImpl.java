package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.warehouse.NoOrderBookingException;
import ru.yandex.practicum.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feignClient.OrderFeignClient;
import ru.yandex.practicum.mapper.OrderBookingMapper;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepo;
    private final OrderBookingRepository orderBookingRepo;
    private final WarehouseMapper warehouseMapper;
    private final OrderBookingMapper orderBookingMapper;

    private final OrderFeignClient orderFeignClient;

    private AddressDto warehouseAddress = setAddress();

    @Transactional
    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest product) {
        if (warehouseRepo.existsById(product.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с id=" + product.getProductId() + " уже есть на складе");
        }
        WarehouseProduct warehouseProduct = warehouseMapper.toEntity(product);
        log.info("Добавляем в БД склада новый товар");
        warehouseRepo.save(warehouseProduct);
    }

    @Transactional
    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Проверяем наличие товара на складе...");
        Map<UUID, Integer> productsInCart = shoppingCart.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepo.findAllById(productsInCart.keySet());
        checkProductQuantity(productsInCart, warehouseProducts);
        log.info("Товары из корзины в полном объёме имеются в наличии на складе");
        return buildBookedProductsDto(warehouseProducts);
    }

    @Transactional
    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = findWarehouseProductById(request.getProductId());
        log.info("Изменяем количество товара на складе на {} единицы", request.getQuantity());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepo.save(product);
        log.info("Товара с id={} на складе стало: {} штук", request.getProductId(), request.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseAddress;
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest productsForOrder) {
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        List<WarehouseProduct> warehouseProducts = warehouseRepo.findAllById(productsForOrder.getProducts().keySet());

        for (var warehouseProduct : warehouseProducts) {
            if (productsForOrder.getProducts().containsKey(warehouseProduct.getProductId())) {
                int productForOrderQuantity = productsForOrder.getProducts().get(warehouseProduct.getProductId());
                if (warehouseProduct.getQuantity() < productForOrderQuantity) {
                    String message = "На складе нет нужного количества товара с id=" + warehouseProduct.getProductId();
                    throw new ProductInShoppingCartLowQuantityInWarehouse(message);
                }
                warehouseProduct.setQuantity(warehouseProduct.getQuantity() - productForOrderQuantity);
            }
            log.info("Остаток товара с id={} на складе: {}", warehouseProduct.getProductId(), warehouseProduct.getQuantity());
            deliveryWeight += warehouseProduct.getWeight();
            deliveryVolume += getWarehouseProductVolume(warehouseProduct);
            if (warehouseProduct.isFragile()) {
                fragile = true;
            }
        }

        warehouseRepo.saveAll(warehouseProducts);

        var orderBooking = OrderBooking.builder()
                .orderId(productsForOrder.getOrderId())
                .products(productsForOrder.getProducts())
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
        orderFeignClient.assembly(productsForOrder.getOrderId());
        return orderBookingMapper.toDto(orderBookingRepo.save(orderBooking));
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDelivery) {
        OrderBooking orderBooking = orderBookingRepo.findById(shippedToDelivery.getOrderId())
                .orElseThrow(() -> new NoOrderBookingException("В БД нет забронированных товаров для заказа с id=" + shippedToDelivery.getOrderId()));
        orderBooking.setDeliveryId(shippedToDelivery.getDeliveryId());
        orderBookingRepo.save(orderBooking);
        log.info("Товары для заказа с id={} переданы в доставку!", shippedToDelivery.getOrderId());
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Integer> returnProducts) {
        for (var product : returnProducts.entrySet()) {
            UUID id = product.getKey();
            Integer quantity = product.getValue();
            try {
                WarehouseProduct warehouseProduct = findWarehouseProductById(id);
                warehouseProduct.setQuantity(warehouseProduct.getQuantity() + quantity);
                warehouseRepo.save(warehouseProduct);
            } catch (NoSpecifiedProductInWarehouseException e) {
                log.warn("Игнорируем данное исключение: {}", e.getMessage());
            }
        }
        log.info("Товары успешно возвращены на склад!");
    }

    private AddressDto setAddress() {
        String[] addrs = new String[] {"ADDRESS_1", "ADDRESS_2"};
        String currentAddress = addrs[Random.from(new SecureRandom()).nextInt(0, addrs.length)];
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(currentAddress);
        addressDto.setCity(currentAddress);
        addressDto.setStreet(currentAddress);
        addressDto.setHouse(currentAddress);
        addressDto.setFlat(currentAddress);
        return addressDto;
    }

    private void checkProductQuantity(Map<UUID, Integer> cartProds, List<WarehouseProduct> warehouseProds) {
        List<UUID> shortage = new ArrayList<>();
        for (var prod : warehouseProds) {
            if (prod.getQuantity() < cartProds.get(prod.getProductId())) {
                shortage.add(prod.getProductId());
            }
        }
        log.info("Товары, которых не хватает на складе: {}", shortage);
        if (!shortage.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Товары из корзины не находятся в требуемом " +
                    "количестве на складе: " + shortage);
        }
    }

    private BookedProductsDto buildBookedProductsDto(List<WarehouseProduct> products) {
        BookedProductsDto result = new BookedProductsDto();
        for (var product : products) {
            result.setDeliveryWeight(result.getDeliveryWeight() + product.getWeight());
            result.setDeliveryVolume(result.getDeliveryVolume()
                    + product.getWidth() * product.getHeight() * product.getDepth());
            if (product.isFragile()) {
                result.setFragile(true);
            }
        }
        log.info("Общие сведения о зарезервированных товарах по корзине: {}", result);
        return result;
    }

    private WarehouseProduct findWarehouseProductById(UUID id) {
        return warehouseRepo.findById(id)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товара с id=" + id + " нет на складе!"));
    }

    private double getWarehouseProductVolume(WarehouseProduct product) {
        return product.getWidth() * product.getHeight() * product.getDepth();
    }
}
