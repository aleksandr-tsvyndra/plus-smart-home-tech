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
        log.error("Ошибка валидации!", e);
        return new ErrorResponse(e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST.toString(),
                "Произошла ошибка валидации данных.",
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class,
            NoSpecifiedProductInWarehouseException.class
    })
    public ErrorResponse handleProductWarehouseException(final Exception e) {
        log.error("Ошибка, связанная с товаром на складе!", e);
        return new ErrorResponse(e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST.toString(),
                "Произошла ошибка, связанная с товаром на складе.",
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleGenericException(final Exception e) {
        log.error("Внутренняя ошибка сервера!", e);
        return new ErrorResponse(e.getCause(),
                e.getStackTrace(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Произошла ошибка на стороне сервера.",
                e.getMessage(),
                e.getSuppressed(),
                e.getLocalizedMessage());
    }
}
