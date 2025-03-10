package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.dto.CategoryDto;
import com.oldvabik.internetshop.mapper.CategoryMapper;
import com.oldvabik.internetshop.model.Category;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.repository.CategoryRepository;
import com.oldvabik.internetshop.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryMapper categoryMapper,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productRepository = productRepository;
    }

    public Category createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Optional<Category> categoryOptional = categoryRepository.findByName(category.getName());
        if (categoryOptional.isPresent()) {
            throw new IllegalStateException(
                    String.format("Category with name %s already exists", category.getName())
            );
        }
        return categoryRepository.save(category);
    }

    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<Category> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(category);
    }

    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Category with id %s does not exist", id)
            );
        }
        Category category = optionalCategory.get();
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            Optional<Category> foundCategory = categoryRepository.findByName(categoryDto.getName());
            if (foundCategory.isPresent()) {
                throw new IllegalStateException(
                        String.format("Category with name %s already exists", categoryDto.getName())
                );
            }
            category.setName(categoryDto.getName());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Category with id %s does not exist", id)
            );
        }
        categoryRepository.delete(optionalCategory.get());
    }

    public ResponseEntity<List<Product>> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<Product> getProductById(Long categoryId, Long productId) {
        Product product = productRepository.findByIdAndCategoryId(productId, categoryId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(product);
    }

}
