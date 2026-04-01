package ru.yandex.practicum.commerce.dto.shopping.store;

import java.util.List;

/**
 * Сериализация постраничного ответа Spring Data {@code Page<ProductDto>}.
 */
public record PageProductDto(
        Long totalElements,
        Integer totalPages,
        Boolean first,
        Boolean last,
        Integer size,
        List<ProductDto> content,
        Integer number,
        List<SortObject> sort,
        Integer numberOfElements,
        PageableObject pageable,
        Boolean empty
) {
}
