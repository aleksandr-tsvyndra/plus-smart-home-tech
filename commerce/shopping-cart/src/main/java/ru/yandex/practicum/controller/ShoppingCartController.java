package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam(name = "username") String username) {
        log.info("Получение актуальной корзины для авторизованного юзера {}", username);
        return cartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam(name = "username") String username,
                                                    @RequestBody Map<UUID, Integer> products) {
        log.info("Добавление в корзину юзера {} следующих товаров: {}", username, products);
        return cartService.addProductToShoppingCart(username, products);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateShoppingCart(@RequestParam(name = "username") String username) {
        log.info("Деактивирование корзины товаров для юзера {}", username);
        cartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromShoppingCart(@RequestParam(name = "username") String username,
                                                         @RequestBody List<UUID> productsId) {
        log.info("Удаление из корзины юзера {} товаров с id: {}", username, productsId);
        return cartService.removeProductFromShoppingCart(username, productsId);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantityInShoppingCart(
            @RequestParam(name = "username") String username,
            @RequestBody ChangeProductQuantityRequest productQuantity) {
        log.info("Изменение количества товаров в корзине юзера {}", username);
        return cartService.changeProductQuantityInShoppingCart(username, productQuantity);
    }
}
