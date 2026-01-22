package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreApi {

    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> findAllByProductCategory(@RequestParam(name = "category") ProductCategory category,
                                              Pageable pageable);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto findProductById(@PathVariable("productId") String productId);

    @PutMapping("/api/v1/shopping-store")
    ProductDto addNewProduct(@Valid @RequestBody ProductDto product);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@Valid @RequestBody ProductDto product);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    Boolean removeProductById(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

}
