package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.entity.Product;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.mapper.ProductMapper;
import ru.yandex.practicum.product.repository.CategoryRepository;
import ru.yandex.practicum.product.repository.ProductRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDto getProductById(Long id) {
        Product product = getProductByIdOrThrow(id);
        if (!Boolean.TRUE.equals(product.getActive())) {
            log.warn("User requested not active product: {}", product);
        }
        return mapper.toDto(product);
    }

    private Product getProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product with id %s not found", id)));
    }

    @Override
    public List<ProductDto> getProducts() {
        return productRepository.findProductsByActiveIsTrue().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findProductsByCategory_idAndActiveIsTrue(categoryId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> searchProductsByName(String searchString) {
        return productRepository.findProductsByNameContainingAndActiveIsTrue(searchString).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request);
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category with id " + request.categoryId() + " not found"));
        }

        Product product = mapper.fromDto(request);
        product.setCategory(category);
        product = productRepository.save(product);
        log.info("Saved new product: {}", product);
        return mapper.toDto(product);
    }

    @Override
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with id {}: {}", id, request);
        Product dbProduct = getProductByIdOrThrow(id);
        Product updatedProduct = mapper.fromDto(dbProduct, request);

        updatedProduct = productRepository.save(updatedProduct);
        log.info("Product was updated: {}", updatedProduct);
        return mapper.toDto(updatedProduct);
    }

}
