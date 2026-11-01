package ru.yandex.practicum.order.service;

import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(CreateOrderRequest request);

    List<OrderDto> getAllOrders();

    OrderDto getOrderById(Long id);

    List<OrderDto> getOrdersByEmail(String email);
}
