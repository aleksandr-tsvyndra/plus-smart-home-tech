package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingStoreApi;
import ru.yandex.practicum.dto.shoppingStore.PageResponse;
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
public class ShoppingStoreController implements ShoppingStoreApi {
    private final ShoppingStoreService storeService;

    @Override
    public PageResponse<ProductDto> findProductByCategory(ProductCategory category, Pageable pageable) {
        log.info("Получение списка товаров по категории в пагинированном виде");
        Page<ProductDto> products = storeService.findAllByProductCategory(category, pageable);
        log.info("Возвращаемый список товаров категории {}: {}", category, products.getContent());
        return new PageResponse<>(products);
    }

    @Override
    public ProductDto findProductById(String productId) {
        log.info("Получение сведений по товару из БД с id={}", productId);
        return storeService.findProductById(productId);
    }

    @Override
    public ProductDto addNewProduct(ProductDto product) {
        log.info("Создание нового товара в ассортименте");
        return storeService.addNewProduct(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        log.info("Обновление товара в ассортименте");
        return storeService.updateProduct(product);
    }

    @Override
    public Boolean removeProductById(UUID productId) {
        log.info("Удаление товара из ассортимента магазина с id={}", productId);
        return storeService.removeProductById(productId);
    }

    @Override
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.info("Установка статуса количества товара на складе");
        return storeService.setProductQuantityState(new SetProductQuantityStateRequest(productId, quantityState));
    }
}
