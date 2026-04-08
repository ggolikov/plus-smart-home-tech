package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Запрос на возврат заказа.
 */
public record ProductReturnRequest(
        UUID orderId,
        @NotNull Map<UUID, Long> products
) {
}
