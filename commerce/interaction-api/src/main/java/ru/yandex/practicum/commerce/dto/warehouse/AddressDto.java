package ru.yandex.practicum.commerce.dto.warehouse;

/**
 * Представление адреса в системе.
 */
public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {
}
