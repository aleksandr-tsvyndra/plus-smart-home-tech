package ru.yandex.practicum.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        log.error("Ошибка валидации!", e);
        return ResponseEntity.badRequest().body("Ошибка валидации входных данных: " + e.getMessage());
    }

    @ExceptionHandler(IllegalTypeEventException.class)
    public ResponseEntity<String> handleIllegalTypeEventException(IllegalTypeEventException e) {
        log.error("Ошибка при попытке получить маппер по типу события!", e);
        return ResponseEntity.badRequest().body("Ошибка при обращении к сервису: " + e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleGenericException(final Exception e) {
        log.error("Внутренняя ошибка сервера!", e);
        return ResponseEntity.internalServerError().body("Ошибка на сервере: " + e.getMessage());
    }
}
