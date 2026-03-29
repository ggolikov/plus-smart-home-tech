package ru.yandex.practicum.commerce.dto.shopping.store;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Товар, продаваемый в интернет-магазине.
 */
public record ProductDto(
        UUID productId,
        @NotBlank String productName,
        @NotBlank String description,
        String imageSrc,
        @NotNull QuantityState quantityState,
        @NotNull ProductState productState,
        ProductCategory productCategory,
        @NotNull @DecimalMin("1") BigDecimal price
) {
}
