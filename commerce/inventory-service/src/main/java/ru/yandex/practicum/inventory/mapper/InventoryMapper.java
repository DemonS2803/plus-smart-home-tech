package ru.yandex.practicum.inventory.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.InventoryRecord;

@Component
public class InventoryMapper {

    public InventoryRecord fromDto(UpdateInventoryRequest request) {
        InventoryRecord record = new InventoryRecord();
        record.setProductId(request.productId());
        record.setQuantity(request.quantity());
        record.setReservedQuantity(0);
        return record;
    }

    public InventoryDto toDto(InventoryRecord record) {
        return new InventoryDto(
                record.getId(),
                record.getProductId(),
                record.getQuantity(),
                record.getReservedQuantity(),
                record.getAvailableQuantity()
        );
    }
}
