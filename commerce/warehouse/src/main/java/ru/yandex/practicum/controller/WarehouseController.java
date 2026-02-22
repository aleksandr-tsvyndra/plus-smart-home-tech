package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {
    private final WarehouseService warehouseService;

    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest product) {
        log.info("Добавление нового товара на склад: {}", product);
        warehouseService.addNewProductInWarehouse(product);
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCart) {
        log.info("Проверка количества товаров на складе для данной корзины: {}", shoppingCart);
        return warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCart);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Запрос на увеличение единиц товара c id={}", request.getProductId());
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Запрос на предоставление адреса склада для расчёта доставки");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest productsForOrder) {
        log.info("Запрос на сборку товаров к заказу для подготовки к отправке: {}", productsForOrder);
        return warehouseService.assemblyProductsForOrder(productsForOrder);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDelivery) {
        log.info("Запрос на передачу товаров в доставку: {}", shippedToDelivery);
        warehouseService.shippedToDelivery(shippedToDelivery);
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> returnProducts) {
        log.info("Запрос на приём возврата товаров на склад: {}", returnProducts);
        warehouseService.acceptReturn(returnProducts);
    }
}
