package ru.yandex.practicum.commerce.dto.payment;

/**
 * Обобщённое тело ошибок payment API (400, 404 и др.) для десериализации ответов сервера.
 */
public record PaymentErrorResponse(
        String userMessage,
        String message,
        String httpStatus
) {
}
