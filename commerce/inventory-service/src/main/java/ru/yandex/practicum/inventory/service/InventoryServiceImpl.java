package ru.yandex.practicum.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.InventoryRecord;
import ru.yandex.practicum.inventory.exception.InsufficientStockException;
import ru.yandex.practicum.inventory.exception.NotFoundException;
import ru.yandex.practicum.inventory.mapper.InventoryMapper;
import ru.yandex.practicum.inventory.repository.InventoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper mapper;

    @Override
    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        log.info("Getting inventory for product: {}", productId);
        InventoryRecord record = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException(String.format("Inventory for product %s not found", productId)));
        return mapper.toDto(record);
    }

    @Override
    public InventoryDto createInventory(UpdateInventoryRequest request) {
        log.info("Creating inventory for product {}: quantity={}", request.productId(), request.quantity());
        if (inventoryRepository.existsByProductId(request.productId())) {
            log.warn("Inventory for product {} already exists", request.productId());
            throw new IllegalArgumentException("Inventory for product " + request.productId() + " already exists");
        }
        InventoryRecord record = mapper.fromDto(request);
        record = inventoryRepository.save(record);
        log.info("Created inventory for product {}: id={}", request.productId(), record.getId());
        return mapper.toDto(record);
    }

    @Override
    public InventoryDto updateInventory(UpdateInventoryRequest request) {
        log.info("Updating inventory for product {}: quantity={}", request.productId(), request.quantity());
        InventoryRecord record = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException(String.format("Inventory for product %s not found", request.productId())));
        record.setQuantity(request.quantity());
        record = inventoryRepository.save(record);
        log.info("Updated inventory for product {}: quantity={}", request.productId(), record.getQuantity());
        return mapper.toDto(record);
    }

    @Override
    public ReserveResponse reserveInventory(ReserveRequest request) {
        log.info("Reserving {} units for product {}", request.quantity(), request.productId());
        InventoryRecord dbRecord = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException(String.format("Inventory for product %s not found", request.productId())));

        int availableQuantity = dbRecord.getAvailableQuantity();
        if (availableQuantity < request.quantity()) {
            log.warn("Insufficient stock for product {}: available={}, requested={}",
                    request.productId(), availableQuantity, request.quantity());
            throw new InsufficientStockException(String.format(
                    "Unable to reserve inventory. Available: %d, Requested: %d",
                    availableQuantity, request.quantity()));
        }

        dbRecord.setReservedQuantity(dbRecord.getReservedQuantity() + request.quantity());
        dbRecord = inventoryRepository.save(dbRecord);
        log.info("Reserved {} units for product {}. Available: {}",
                request.quantity(), request.productId(), dbRecord.getAvailableQuantity());

        return new ReserveResponse(true, dbRecord.getAvailableQuantity(), "Product reserved successfully");
    }
}
