package ru.yandex.practicum.inventory.service;

import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;

import java.util.List;

public interface InventoryService {

    List<InventoryDto> getAllInventory();

    InventoryDto getInventoryByProductId(Long productId);

    InventoryDto createInventory(UpdateInventoryRequest request);

    InventoryDto updateInventory(UpdateInventoryRequest request);

    ReserveResponse reserveInventory(ReserveRequest request);
}
