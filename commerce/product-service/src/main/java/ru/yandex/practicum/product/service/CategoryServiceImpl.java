package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.mapper.CategoryMapper;
import ru.yandex.practicum.product.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryMapper.toDto(getCategoryByIdOrThrow(id));
    }

    private Category getCategoryByIdOrThrow(Long id) {
        return Optional.of(categoryRepository.getReferenceById(id))
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", id)));
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest) {
        log.info("Creating new category: {}", createCategoryRequest);
        Category category = categoryMapper.fromDto(createCategoryRequest);

        category = categoryRepository.save(category);
        log.info("New category was successfully saved: {}", category);
        return categoryMapper.toDto(category);
    }

}
