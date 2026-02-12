package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.order.NoOrderFoundException;
import ru.yandex.practicum.exception.shoppingCart.DeactivatedShoppingCartException;
import ru.yandex.practicum.exception.shoppingCart.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.shoppingCart.NotAuthorizedUserException;
import ru.yandex.practicum.exception.shoppingCart.ShoppingCartNotFoundException;
import ru.yandex.practicum.exception.shoppingStore.ProductNotFoundException;
import ru.yandex.practicum.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class})
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации данных!", e);
        return new ErrorResponse("Ошибка валидации данных: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ProductNotFoundException.class, NoOrderFoundException.class})
    public ErrorResponse handleProductNotFoundException(final Exception e) {
        log.error("Ошибка при попытке обратиться к несуществующему ресурсу!", e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            NoProductsInShoppingCartException.class,
            DeactivatedShoppingCartException.class,
            ShoppingCartNotFoundException.class
    })
    public ErrorResponse handleShoppingCartException(final Exception e) {
        log.error("Ошибка при взаимодействии с корзиной!", e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedUserException.class)
    public ErrorResponse handleNotAuthorizedUser(final NotAuthorizedUserException e) {
        log.error("Ошибка, связанная с авторизацией пользователя!", e);
        return new ErrorResponse("Ошибка авторизации пользователя: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class,
            NoSpecifiedProductInWarehouseException.class
    })
    public ErrorResponse handleProductWarehouseException(final Exception e) {
        log.error("Ошибка, связанная с товаром на складе!", e);
        return new ErrorResponse("Ошибка, связанная с товаром на складе: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleGenericException(final Exception e) {
        log.error("Внутренняя ошибка сервера!", e);
        return new ErrorResponse("Ошибка на стороне сервера: " + e.getMessage());
    }
}
