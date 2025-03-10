package com.oldvabik.internetshop.mapper;

import com.oldvabik.internetshop.dto.CategoryDto;
import com.oldvabik.internetshop.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public Category toEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

}
