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
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepo;
    private final WarehouseMapper warehouseMapper;

    private AddressDto warehouseAddress = setAddress();

    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest product) {
        UUID id = UUID.fromString(product.getProductId());
        if (warehouseRepo.existsById(id)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с id=" + id + " уже есть на складе");
        }
        WarehouseProduct warehouseProduct = warehouseMapper.toEntity(product);
        log.info("Добавляем в БД склада новый товар");
        warehouseRepo.save(warehouseProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        Map<UUID, Integer> productsInCart = shoppingCart.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepo.findAllById(productsInCart.keySet());
        Map<UUID, WarehouseProduct> warehouseProductsMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        checkActiveProductsInWarehouse(productsInCart.keySet(), warehouseProductsMap.keySet());
        checkProductQuantity(productsInCart, warehouseProductsMap);
        return buildBookedProductsDto(warehouseProducts);
    }

    @Override
    public void addMoreProductInWarehouse(AddProductToWarehouseRequest request) {
        UUID id = UUID.fromString(request.getProductId());
        WarehouseProduct product = warehouseRepo.findById(id)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товара с id=" + id + " нет на складе"));
        log.info("Изменяем количество товара на складе на {} единицы", request.getQuantity());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepo.save(product);
        log.info("Товара с id={} на складе стало: {} штук", id, request.getQuantity());
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

    private void checkActiveProductsInWarehouse(Set<UUID> cartProds, Set<UUID> warehouseProds) {
        cartProds.removeAll(warehouseProds);
        log.info("Товары из корзины, которых нет на складе: {}", cartProds);
        if (!cartProds.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("На складе нет следующих товаров: " + cartProds);
        }
    }

    private void checkProductQuantity(Map<UUID, Integer> cartProds, Map<UUID, WarehouseProduct> warehouseProds) {
        List<UUID> shortage = new ArrayList<>();
        for (var id : cartProds.keySet()) {
            if (cartProds.get(id) > warehouseProds.get(id).getQuantity()) {
                shortage.add(id);
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
            if (product.getFragile()) {
                result.setFragile(true);
            }
        }
        log.info("Общие сведения о зарезервированных товарах по корзине: {}", result);
        return result;
    }
}
