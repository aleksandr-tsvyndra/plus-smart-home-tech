package ru.yandex.practicum.exception.shoppingCart;

public class DeactivatedShoppingCartException extends RuntimeException {
    public DeactivatedShoppingCartException(String message) {
        super(message);
    }
}
