package ru.yandex.practicum.product.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;
import ru.yandex.practicum.product.entity.Category;

@Component
public class CategoryMapper {

    public Category fromDto(CreateCategoryRequest request) {
        Category category = new Category();

        if (request.name() != null && !request.name().isEmpty()) {
            category.setName(request.name());
        }
        if (request.description() != null && !request.description().isEmpty()) {
            category.setDescription(request.description());
        }

        return category;
    }

    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}
