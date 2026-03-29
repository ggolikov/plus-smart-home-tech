package ru.yandex.practicum.commerce.warehouse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;

@ConfigurationProperties(prefix = "warehouse.address")
public record WarehouseAddressProperties(
        String country,
        String city,
        String street,
        String house,
        String flat
) {

    public AddressDto toAddressDto() {
        return new AddressDto(country, city, street, house, flat);
    }
}
