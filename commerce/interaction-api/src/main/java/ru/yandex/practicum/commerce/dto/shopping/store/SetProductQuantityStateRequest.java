package ru.yandex.practicum.commerce.dto.shopping.store;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Запрос на изменение статуса остатка товара.
 */
public record SetProductQuantityStateRequest(
        @NotNull UUID productId,
        @NotNull QuantityState quantityState
) {
}
