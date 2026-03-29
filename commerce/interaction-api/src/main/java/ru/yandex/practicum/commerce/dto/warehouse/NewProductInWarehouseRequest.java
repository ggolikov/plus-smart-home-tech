package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Запрос на добавление нового товара на склад.
 */
public record NewProductInWarehouseRequest(
        @NotNull UUID productId,
        Boolean fragile,
        @NotNull @Valid DimensionDto dimension,
        @NotNull @DecimalMin("1") Double weight
) {
}
