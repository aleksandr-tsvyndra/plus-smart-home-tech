package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
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
    private final WarehouseMapper warehouseMapper;

    private AddressDto warehouseAddress = setAddress();

    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest product) {
        if (warehouseRepo.existsById(product.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с id=" + product.getProductId() + " уже есть на складе");
        }
        WarehouseProduct warehouseProduct = warehouseMapper.toEntity(product);
        log.info("Добавляем в БД склада новый товар");
        warehouseRepo.save(warehouseProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Проверяем наличие товара на складе...");
        Map<UUID, Integer> productsInCart = shoppingCart.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepo.findAllById(productsInCart.keySet());
        checkProductQuantity(productsInCart, warehouseProducts);
        log.info("Товары из корзины в полном объёме имеются в наличии на складе");
        return buildBookedProductsDto(warehouseProducts);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepo.findById(request.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Товара с id=" + request.getProductId() + " нет на складе!"));
        log.info("Изменяем количество товара на складе на {} единицы", request.getQuantity());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepo.save(product);
        log.info("Товара с id={} на складе стало: {} штук", request.getProductId(), request.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseAddress;
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
        BookedProductsDto result = new BookedProductsDto(0.0, 0.0, false);
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
}
