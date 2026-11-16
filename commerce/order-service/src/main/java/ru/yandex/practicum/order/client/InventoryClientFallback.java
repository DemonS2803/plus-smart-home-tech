package ru.yandex.practicum.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.*;

import java.util.List;

@Slf4j
@Component
public class InventoryClientFallback implements InventoryClient {

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        log.warn("InventoryService is unavailable, getInventoryByProductId({}) — fallback", productId);
        throw new RuntimeException("InventoryService is unavailable");
    }

}
