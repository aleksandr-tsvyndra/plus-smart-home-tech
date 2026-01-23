package ru.yandex.practicum.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.ProductState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable) {
        log.info("Ищем товары категории {} в БД...", productCategory);
        Page<Product> products = productRepository.findAllByProductCategory(productCategory, pageable);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("Не найдено товаров категории: " + productCategory);
        }
        return products.map(productMapper::toDto);
    }

    @Override
    public ProductDto findProductById(String productId) {
        UUID id = UUID.fromString(productId);
        log.info("Ищем товар в БД...");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товара с id=" + id + " нет в наличии"));
        log.info("Мапим найденный товар в ProductDto и возвращаем его в http-ответе");
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto addNewProduct(ProductDto dto) {
        if (dto.getProductId() != null) {
            throw new ValidationException("При создании товара поле productId должно быть null");
        }
        Product newProduct = productRepository.save(productMapper.toEntity(dto));
        log.info("В БД магазина добавлен новый товар: {}", newProduct);
        return productMapper.toDto(newProduct);
    }

    @Override
    public ProductDto updateProduct(ProductDto dto) {
        if (dto.getProductId() == null || dto.getProductId().isBlank()) {
            throw new ValidationException("При обновлении товара поле productId не может быть пустым");
        }
        UUID productId = UUID.fromString(dto.getProductId());
        if (productRepository.findById(productId).isEmpty()) {
            throw new ProductNotFoundException("В БД нет товара с id=" + productId + " для обновления");
        }
        Product product = productMapper.toEntity(dto);
        product.setProductId(productId);
        log.info("Обновляем в БД товар с id={}...", productId);
        Product updatedProduct = productRepository.save(product);
        log.info("Товар успешно обновлен: {}", updatedProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public Boolean removeProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("В БД нет товара с id=" + productId + " для удаления"));
        log.info("Удаляем товар с id={}...", productId);
        if (product.getProductState() != ProductState.DEACTIVATE) {
            product.setProductState(ProductState.DEACTIVATE);
            product = productRepository.save(product);
            log.info("Товар с id={} успешно удалён", productId);
        }
        return product.getProductState() == ProductState.DEACTIVATE;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
        Product product = productRepository.findById(quantityStateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("В БД нет товара с id="
                        + quantityStateRequest.getProductId() + " для изменения доступного количества"));
        log.info("Меняем доступное количество товара с id={}", quantityStateRequest.getProductId());
        product.setQuantityState(quantityStateRequest.getQuantityState());
        product = productRepository.save(product);
        log.info("Товар с обновленным доступным количеством: {}", product);
        return product.getQuantityState() == quantityStateRequest.getQuantityState();
    }
}
