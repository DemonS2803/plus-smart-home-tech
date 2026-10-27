package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.exception.NotFoundException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;

    @Override
    public OrderDto createOrder(CreateOrderRequest request) {
        log.info("Creating new order for customer: {} ({})", request.customerName(), request.customerEmail());
        Order order = mapper.fromDto(request);

        order = orderRepository.save(order);
        log.info("Order created with id: {}", order.getId());
        return mapper.toDto(order);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        log.info("Getting order by id: {}", id);
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order with  id %s not found", id)));
        return mapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByEmail(String email) {
        log.info("Getting orders for email: {}", email);
        return orderRepository.findByCustomerEmail(email).stream()
                .map(mapper::toDto)
                .toList();
    }
}
