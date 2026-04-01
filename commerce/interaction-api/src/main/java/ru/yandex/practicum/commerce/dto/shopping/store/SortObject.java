package ru.yandex.practicum.commerce.dto.shopping.store;

public record SortObject(
        String direction,
        String nullHandling,
        Boolean ascending,
        String property,
        Boolean ignoreCase
) {
}
