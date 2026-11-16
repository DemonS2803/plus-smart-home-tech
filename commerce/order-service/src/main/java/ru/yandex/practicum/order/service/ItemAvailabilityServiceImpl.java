package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.client.InventoryClient;
import ru.yandex.practicum.order.client.ProductClient;
import ru.yandex.practicum.order.dto.InventoryDto;
import ru.yandex.practicum.order.dto.OrderItemRequest;
import ru.yandex.practicum.order.dto.ProductDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemAvailabilityServiceImpl implements ItemAvailabilityService {

    private final InventoryClient inventoryClient;
    private final ProductClient productClient;

    @Override
    public boolean checkItemsAvailable(List<OrderItemRequest> items) {
        log.info("Checking availability for {} items", items.size());

        for (OrderItemRequest item : items) {
            if (!isProductActive(item.productId())) {
                log.warn("Product not active or not found: productId={}", item.productId());
                return false;
            }

            if (!isInventoryAvailable(item.productId(), item.quantity())) {
                log.warn("Insufficient stock: productId={}, requested={}",
                        item.productId(), item.quantity());
                return false;
            }
        }

        log.info("All items are available");
        return true;
    }

    private boolean isProductActive(Long productId) {
        try {
            ProductDto product = productClient.getProductById(productId);
            if (product == null) {
                log.warn("Product not found via client: productId={}", productId);
                return false;
            }
            if (Boolean.FALSE.equals(product.active())) {
                log.warn("Product is inactive: productId={}", productId);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Product service unavailable for productId={}. "
                    + "Assuming product is available; validation will happen during reservation.", productId, e);
            return true;
        }
    }

    private boolean isInventoryAvailable(Long productId, Integer quantity) {
        try {
            InventoryDto inventory = inventoryClient.getInventoryByProductId(productId);
            if (inventory == null) {
                log.warn("No inventory record found: productId={}", productId);
                return false;
            }

            Integer availableQuantity = inventory.availableQuantity();
            if (availableQuantity == null || availableQuantity < quantity) {
                log.warn("Insufficient stock: productId={}, available={}, requested={}",
                        productId, availableQuantity, quantity);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Inventory service unavailable for productId={}. "
                    + "Order will proceed; reservation will be validated during order creation.", productId, e);
            return true;
        }
    }
}
