package ru.yandex.practicum.commerce.dto.shopping;

/**
 * Тело ответа при ошибках API корзины (401, 400 и др.), совместимо с полезной нагрузкой сервера.
 */
public record ShoppingCartErrorResponse(
        String userMessage,
        String message,
        String httpStatus
) {
}
