package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.ShoppingCartApi;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartApi {
    private final ShoppingCartService cartService;

    @Override
    public ShoppingCartDto getUserShoppingCart(String username) {
        log.info("Получение актуальной корзины для авторизованного юзера {}", username);
        return cartService.getUserShoppingCart(username);
    }

    @Override
    public ShoppingCartDto putProductInShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Добавление в корзину юзера {} следующих товаров: {}", username, products);
        return cartService.putProductInShoppingCart(username, products);
    }

    @Override
    public void deactivateUserShoppingCart(String username) {
        log.info("Деактивирование корзины товаров для юзера {}", username);
        cartService.deactivateUserShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productsId) {
        log.info("Удаление из корзины юзера {} товаров с id: {}", username, productsId);
        return cartService.removeProductFromShoppingCart(username, productsId);
    }

    @Override
    public ShoppingCartDto changeProductQuantityInShoppingCart(String username,
                                                               ChangeProductQuantityRequest productQuantity) {
        log.info("Изменение количества товаров в корзине юзера {}", username);
        return cartService.changeProductQuantityInShoppingCart(username, productQuantity);
    }
}
