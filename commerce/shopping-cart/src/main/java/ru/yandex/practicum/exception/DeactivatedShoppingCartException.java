package ru.yandex.practicum.exception;

public class DeactivatedShoppingCartException extends RuntimeException {
    public DeactivatedShoppingCartException(String message) {
        super(message);
    }
}
