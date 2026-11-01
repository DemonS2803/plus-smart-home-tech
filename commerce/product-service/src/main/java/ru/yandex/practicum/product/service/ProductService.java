package ru.yandex.practicum.product.service;

import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    ProductDto getProductById(Long id);

    List<ProductDto> getProducts();

    List<ProductDto> getProductsByCategory(Long categoryId);

    List<ProductDto> searchProductsByName(String searchString);

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(Long id, UpdateProductRequest request);

}
