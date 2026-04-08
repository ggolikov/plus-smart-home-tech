package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;

/**
 * Запрос на создание заказа.
 * <p>Поле {@code username} не входит в минимальную OpenAPI-схему, но нужно для привязки заказа к
 * пользователю при вызове {@code GET /api/v1/order?username=...}.</p>
 */
public record CreateNewOrderRequest(
        String username,
        @NotNull @Valid ShoppingCartDto shoppingCart,
        @NotNull @Valid AddressDto deliveryAddress
) {
}
