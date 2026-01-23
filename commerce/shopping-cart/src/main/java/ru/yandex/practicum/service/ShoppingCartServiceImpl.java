package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingCart.CartState;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.exception.DeactivatedShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.feignClient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepo;
    private final ShoppingCartMapper shoppingCartMapper;

    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        return shoppingCartMapper.toDto(getShoppingCartByUsername(username));
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        checkUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCartByUsername(username);
        putProductsToUserShoppingCart(shoppingCart, products);
        log.info("В корзину юзера добавился новый товар: {}", products);
        warehouseFeignClient.checkProductQuantityInWarehouse(shoppingCartMapper.toDto(shoppingCart));
        shoppingCart = shoppingCartRepo.save(shoppingCart);
        log.info("Сохранили обновлённую корзину в БД");
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        if (shoppingCart.getCartState() == CartState.DEACTIVATE) {
            throw new DeactivatedShoppingCartException("Деактивация невозможна! Корзина уже была деактивирована.");
        }
        shoppingCart.setCartState(CartState.DEACTIVATE);
        shoppingCartRepo.save(shoppingCart);
        log.info("Корзина юзера {} деактивирована", username);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productsId) {
        checkUsername(username);
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        if (shoppingCart.getCartState() == CartState.DEACTIVATE) {
            throw new DeactivatedShoppingCartException("Нельзя удалить товар из деактивированной корзины!");
        }
        if (!shoppingCart.getProducts().isEmpty()) {
            for (var id : productsId) {
                shoppingCart.getProducts().remove(id);
            }
            shoppingCart = shoppingCartRepo.save(shoppingCart);
            log.info("Товар был успешно удалён из корзины");
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto changeProductQuantityInShoppingCart(String username,
                                                               ChangeProductQuantityRequest prodQuantity) {
        checkUsername(username);
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        if (shoppingCart.getCartState() == CartState.DEACTIVATE) {
            throw new DeactivatedShoppingCartException("Нельзя менять количество товара в деактивированной корзине!");
        }
        if (shoppingCart.getProducts().containsKey(prodQuantity.getProductId())) {
            shoppingCart.getProducts().put(prodQuantity.getProductId(), prodQuantity.getNewQuantity());
            log.info("Товар с id {} обновил количество: {}", prodQuantity.getProductId(), prodQuantity.getNewQuantity());
            warehouseFeignClient.checkProductQuantityInWarehouse(shoppingCartMapper.toDto(shoppingCart));
            shoppingCart = shoppingCartRepo.save(shoppingCart);
            log.info("Сохранили корзину с изменённым количеством товара в БД");
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private void checkUsername(String username) {
        log.info("Проверка имени пользователя: {}", username);
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть null или пустым!");
        }
    }

    private ShoppingCart getActiveShoppingCartByUsername(String username) {
        ShoppingCart shoppingCart;
        try {
            shoppingCart = getShoppingCartByUsername(username);
            if (shoppingCart.getCartState() == CartState.DEACTIVATE) {
                throw new DeactivatedShoppingCartException("Нельзя добавить товар в деактивированную корзину!");
            }
        } catch (ShoppingCartNotFoundException e) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUsername(username);
            shoppingCart.setCartState(CartState.ACTIVE);
            shoppingCart.setProducts(new HashMap<>());
        }
        return shoppingCart;
    }

    private ShoppingCart getShoppingCartByUsername(String username) {
        return shoppingCartRepo.findByUsername(username).orElseThrow(
                () -> new ShoppingCartNotFoundException("Корзина не найдена!"));
    }

    private void putProductsToUserShoppingCart(ShoppingCart shoppingCart, Map<UUID, Integer> products) {
        if (products != null) {
            for (var id : products.keySet()) {
                if (shoppingCart.getProducts().containsKey(id)) {
                    shoppingCart.getProducts().put(id, shoppingCart.getProducts().get(id) + products.get(id));
                } else {
                    shoppingCart.getProducts().put(id, products.get(id));
                }
            }
        }
    }
}
