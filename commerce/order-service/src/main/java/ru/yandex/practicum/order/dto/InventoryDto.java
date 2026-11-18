package ru.yandex.practicum.order.dto;


public record InventoryDto(

        Long id,

        Long productId,

        Integer quantity,

        Integer reservedQuantity,

        Integer availableQuantity
) {
}
