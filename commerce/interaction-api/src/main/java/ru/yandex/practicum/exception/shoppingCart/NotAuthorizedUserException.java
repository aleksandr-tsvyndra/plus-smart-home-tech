package ru.yandex.practicum.exception.shoppingCart;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String message) {
        super(message);
    }
}
