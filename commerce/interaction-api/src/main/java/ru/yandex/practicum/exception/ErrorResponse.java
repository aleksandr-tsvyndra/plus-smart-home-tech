package ru.yandex.practicum.exception;

public record ErrorResponse(Throwable cause,
                            StackTraceElement[] stackTrace,
                            String httpStatus,
                            String userMessage,
                            String message,
                            Throwable[] suppressed,
                            String localizedMessage) {
}
