package ru.yandex.practicum.exception.shoppingCart;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
