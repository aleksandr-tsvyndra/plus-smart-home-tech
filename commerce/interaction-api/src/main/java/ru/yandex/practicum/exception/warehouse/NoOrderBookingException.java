package ru.yandex.practicum.exception.warehouse;

public class NoOrderBookingException extends RuntimeException {
    public NoOrderBookingException(String message) {
        super(message);
    }
}
