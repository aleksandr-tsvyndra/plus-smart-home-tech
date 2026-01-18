package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    List<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);

    ProductDto findProductById(String productId);

    ProductDto addNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductById(UUID productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest);

}
