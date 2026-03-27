package ru.yandex.practicum.commerce.dto.shopping;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Запрос на изменение количества единиц товара.
 */
public record ChangeProductQuantityRequest(
        @NotNull
        UUID productId,

        long newQuantity
) {
}
