package ru.yandex.practicum.order.dto;

import lombok.Builder;

@Builder
public record CategoryDto(

        Long id,

        String name,

        String description
) {
}
