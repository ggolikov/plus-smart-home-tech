package ru.yandex.practicum.commerce.delivery.mapper;

import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.delivery.repository.entity.DeliveryEntity;

public final class DeliveryMapper {

    private DeliveryMapper() {
    }

    public static DeliveryDto toDto(DeliveryEntity entity) {
        AddressDto from = new AddressDto(
                entity.getFromCountry(),
                entity.getFromCity(),
                entity.getFromStreet(),
                entity.getFromHouse(),
                entity.getFromFlat()
        );
        AddressDto to = new AddressDto(
                entity.getToCountry(),
                entity.getToCity(),
                entity.getToStreet(),
                entity.getToHouse(),
                entity.getToFlat()
        );
        return new DeliveryDto(
                entity.getDeliveryId(),
                from,
                to,
                entity.getOrderId(),
                entity.getState()
        );
    }
}
