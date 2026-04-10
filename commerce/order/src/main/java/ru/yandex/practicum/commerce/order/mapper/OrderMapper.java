package ru.yandex.practicum.commerce.order.mapper;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.order.repository.entity.OrderEntity;

import java.util.HashMap;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto toDto(OrderEntity entity) {
        return new OrderDto(
                entity.getOrderId(),
                entity.getShoppingCartId(),
                new HashMap<>(entity.getProducts()),
                entity.getPaymentId(),
                entity.getDeliveryId(),
                entity.getState(),
                entity.getDeliveryWeight(),
                entity.getDeliveryVolume(),
                entity.getFragile(),
                entity.getTotalPrice(),
                entity.getDeliveryPrice(),
                entity.getProductPrice()
        );
    }
}
