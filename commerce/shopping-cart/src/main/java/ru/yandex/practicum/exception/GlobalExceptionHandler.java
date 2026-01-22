package ru.yandex.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error("Ошибка валидации данных!", e);
        return new ErrorResponse("Ошибка валидации данных: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ErrorResponse handleNoProductsInShoppingCart(final Exception e) {
        log.error("Ошибка при попытке обратиться к товару, которого нет в корзине!", e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedUserException.class)
    public ErrorResponse handleNotAuthorizedUser(final Exception e) {
        log.error("Ошибка, связанная с авторизацией пользователя!", e);
        return new ErrorResponse("Ошибка авторизации пользователя: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleGenericException(final Exception e) {
        log.error("Внутренняя ошибка сервера!", e);
        return new ErrorResponse("Ошибка на стороне сервера: " + e.getMessage());
    }
}
