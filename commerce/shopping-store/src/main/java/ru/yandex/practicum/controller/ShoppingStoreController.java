package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController("/api/v1/shopping-store")
public class ShoppingStoreController {
    private final ShoppingStoreService storeService;

    @GetMapping
    public List<ProductDto> findAllByProductCategory(
            @RequestParam(name = "category") ProductCategory category,
            Pageable pageable
    ) {
        log.info("Получение списка товаров по категории в пагинированном виде");
        return storeService.findAllByProductCategory(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto findProductById(@PathVariable("productId") String productId) {
        log.info("Получение сведений по товару из БД с id={}", productId);
        return storeService.findProductById(productId);
    }

    @PutMapping
    public ProductDto addNewProduct(@Valid @RequestBody ProductDto product) {
        log.info("Создание нового товара в ассортименте");
        return storeService.addNewProduct(product);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto product) {
        log.info("Обновление товара в ассортименте");
        return storeService.updateProduct(product);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProductById(@RequestBody UUID productId) {
        log.info("Удаление товара из ассортимента магазина с id={}", productId);
        return storeService.removeProductById(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(
            @Valid @RequestBody SetProductQuantityStateRequest quantityStateRequest
    ) {
        log.info("Установка статуса количества товара на складе");
        return storeService.setProductQuantityState(quantityStateRequest);
    }
}
