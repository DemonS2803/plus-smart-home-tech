package ru.yandex.practicum.order.service;

import ru.yandex.practicum.order.dto.OrderItemRequest;

import java.util.List;

public interface ItemAvailabilityService {

    boolean checkItemsAvailable(List<OrderItemRequest> items);

}
