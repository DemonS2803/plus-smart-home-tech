package ru.yandex.practicum.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDto> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(
            @RequestBody @Valid UpdateInventoryRequest request) {
        InventoryDto created = inventoryService.createInventory(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<InventoryDto> updateInventory(
            @RequestBody @Valid UpdateInventoryRequest request) {
        InventoryDto updated = inventoryService.updateInventory(request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponse> reserveInventory(
            @RequestBody @Valid ReserveRequest request) {
        ReserveResponse response = inventoryService.reserveInventory(request);
        return ResponseEntity.ok(response);
    }
}
