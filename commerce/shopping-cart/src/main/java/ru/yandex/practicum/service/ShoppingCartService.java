package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingCart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productsId);

    ShoppingCartDto changeProductQuantityInShoppingCart(String username, ChangeProductQuantityRequest productQuantity);

}
