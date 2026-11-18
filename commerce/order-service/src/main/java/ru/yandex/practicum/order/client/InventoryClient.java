package ru.yandex.practicum.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.order.dto.InventoryDto;

@FeignClient(
        name = "inventory-service",
        fallback = InventoryClientFallback.class
)
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    InventoryDto getInventoryByProductId(@PathVariable("productId") Long productId);

}
