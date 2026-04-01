package ru.yandex.practicum.commerce.dto.shopping;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Корзина товаров в онлайн магазине.
 */
public record ShoppingCartDto(
        @NotNull UUID shoppingCartId,
        @NotNull Map<UUID, Long> products
) {
}
