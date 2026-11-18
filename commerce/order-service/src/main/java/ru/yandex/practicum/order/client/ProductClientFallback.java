package ru.yandex.practicum.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.CategoryDto;
import ru.yandex.practicum.order.dto.ProductDto;

import java.util.List;

@Slf4j
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDto getProductById(Long id) {
        log.warn("ProductService is unavailable, getProductById(id={}) — fallback", id);
        throw new RuntimeException("ProductService is unavailable");
    }

}
