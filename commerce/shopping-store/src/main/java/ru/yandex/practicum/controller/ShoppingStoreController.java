package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {
    private final ShoppingStoreService storeService;

    @GetMapping
    public Page<ProductDto> findProductByCategory(@RequestParam(name = "category") ProductCategory category,
                                                  @PageableDefault(sort = {"productName"}) Pageable pageable) {
        log.info("Получение списка товаров по категории в пагинированном виде");
        Page<ProductDto> products = storeService.findAllByProductCategory(category, pageable);
        log.info("Возвращаемый список товаров категории {}: {}", category, products.getContent());
        return products;
    }

    @GetMapping("/{productId}")
    public ProductDto findProductById(@PathVariable("productId") String productId) {
        log.info("Получение сведений по товару из БД с id={}", productId);
        return storeService.findProductById(productId);
    }

    @ResponseStatus(HttpStatus.CREATED)
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
    public Boolean setProductQuantityState(@RequestParam @NotNull UUID productId,
                                           @RequestParam @NotNull QuantityState quantityState) {
        log.info("Установка статуса количества товара на складе");
        return storeService.setProductQuantityState(new SetProductQuantityStateRequest(productId, quantityState));
    }
}
