package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.constraints.NotNull;

/**
 * Общие сведения о зарезервированных товарах по корзине.
 */
public record BookedProductsDto(
        @NotNull Double deliveryWeight,
        @NotNull Double deliveryVolume,
        @NotNull Boolean fragile
) {
}
