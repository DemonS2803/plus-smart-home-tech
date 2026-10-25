package ru.yandex.practicum.product.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.entity.Product;

@Component
public class ProductMapper {

    public Product fromDto(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setActive(true);
        product.setPrice(request.price());

        if (request.categoryId() != null) {
            Category category = new Category();
            category.setId(request.categoryId());
            product.setCategory(category);
        }
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }

        return product;
    }

    public Product fromDto(Product product, UpdateProductRequest request) {
        if (product == null) {
            product = new Product();
        }

        if (request.name() != null && !request.name().isEmpty()) {
            product.setName(request.name());
        }
        if (request.description() != null && !request.description().isEmpty()) {
            product.setDescription(request.description());
        }
        if (request.categoryId() != null) {
            Category category = new Category();
            category.setId(request.categoryId());
            product.setCategory(category);
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }
        return product;
    }

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
