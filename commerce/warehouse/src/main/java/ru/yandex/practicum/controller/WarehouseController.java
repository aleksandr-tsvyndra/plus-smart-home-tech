package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addNewProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest product) {
        log.info("Добавление нового товара на склад: {}", product);
        warehouseService.addNewProductInWarehouse(product);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityInWarehouse(@Valid @RequestBody ShoppingCartDto shoppingCart) {
        log.info("Проверка количества товаров на складе для данной корзины: {}", shoppingCart);
        return warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCart);
    }

    @PostMapping("/add")
    public void addMoreProductInWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        log.info("Запрос на увеличение единиц товара c id={}", request.getProductId());
        warehouseService.addMoreProductInWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Запрос на предоставление адреса склада для расчёта доставки");
        return warehouseService.getWarehouseAddress();
    }
}
