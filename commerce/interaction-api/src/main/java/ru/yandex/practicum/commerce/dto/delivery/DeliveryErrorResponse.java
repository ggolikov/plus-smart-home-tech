package ru.yandex.practicum.commerce.dto.delivery;

/**
 * Обобщённое тело ошибок delivery API (404 и др.) для десериализации ответов сервера.
 */
public record DeliveryErrorResponse(
        String userMessage,
        String message,
        String httpStatus
) {
}
