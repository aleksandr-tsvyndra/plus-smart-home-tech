package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartState;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
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

    @Override
    public ShoppingCartDto getUserShoppingCart(String username) {
        checkUsername(username);
        return shoppingCartMapper.mapToShoppingCartDto(getActiveShoppingCartByUserName(username));
    }

    @Override
    public ShoppingCartDto putProductInShoppingCart(String username, Map<UUID, Integer> products) {
        checkUsername(username);
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Мапа добавляемых товаров не может быть null или пустой");
        }
        ShoppingCart shoppingCart = getActiveShoppingCartByUserName(username);
        shoppingCart.getProducts().putAll(products);
        log.info("В корзину юзера добавился новый товар: {}", products);
        // здесь должна быть логика, отвечающая за проверку наличия товара на складе
        shoppingCart = shoppingCartRepo.save(shoppingCart);
        log.info("Сохранили обновлённую корзину в БД");
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    public void deactivateUserShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCartByUserName(username);
        shoppingCart.setCartState(ShoppingCartState.DEACTIVATED);
        shoppingCartRepo.save(shoppingCart);
        log.info("Корзина юзера {} деактивирована", username);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productsId) {
        checkUsername(username);
        if (productsId == null || productsId.isEmpty()) {
            throw new IllegalArgumentException("Список удаляемых товаров не может быть null или пустым");
        }
        ShoppingCart shoppingCart = getActiveShoppingCartByUserName(username);
        if (shoppingCart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("В корзине пусто");
        }
        for (var id : productsId) {
            shoppingCart.getProducts().remove(id);
        }
        shoppingCart = shoppingCartRepo.save(shoppingCart);
        log.info("Товар был успешно удалён из корзины");
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto changeProductQuantityInShoppingCart(String username,
                                                               ChangeProductQuantityRequest prodQuantity) {
        checkUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCartByUserName(username);
        if (!shoppingCart.getProducts().containsKey(prodQuantity.getProductId())) {
            throw new NoProductsInShoppingCartException("В корзине нет товара с id=" + prodQuantity.getProductId());
        }
        shoppingCart.getProducts().put(prodQuantity.getProductId(), prodQuantity.getNewQuantity());
        log.info("Товар с id {} обновил количество: {}", prodQuantity.getProductId(), prodQuantity.getNewQuantity());
        // здесь должна быть логика, отвечающая за проверку наличия товара на складе
        shoppingCart = shoppingCartRepo.save(shoppingCart);
        log.info("Сохранили корзину с изменённым количеством товара в БД");
        return shoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    private void checkUsername(String username) {
        log.info("Проверка имени пользователя: {}", username);
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть null или пустым");
        }
    }

    private ShoppingCart getActiveShoppingCartByUserName(String username) {
        var shoppingCartOpt = shoppingCartRepo.findByUsernameAndShoppingCartState(username, ShoppingCartState.ACTIVE);
        ShoppingCart shoppingCart;
        if (shoppingCartOpt.isEmpty()) {
            log.info("У юзера {} нет активной корзины", username);
            shoppingCart = new ShoppingCart();
            shoppingCart.setUsername(username);
            shoppingCart.setCartState(ShoppingCartState.ACTIVE);
            shoppingCart.setProducts(new HashMap<>());
            shoppingCart = shoppingCartRepo.save(shoppingCart);
            log.info("Новая активная корзина юзера: {}", shoppingCart);
        } else {
            shoppingCart = shoppingCartOpt.get();
            log.info("Активная корзина юзера: {}", shoppingCart);
        }
        return shoppingCart;
    }
}
