package ru.yandex.practicum.commerce.dto.order;

/**
 * Обобщённое тело ошибок order API (401, 400 и др.) для десериализации ответов сервера.
 */
public record OrderErrorResponse(
        String userMessage,
        String message,
        String httpStatus
) {
}
