package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.CategoryCache;
import com.oldvabik.internetshop.dto.CategoryDto;
import com.oldvabik.internetshop.exception.AlreadyExistsException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import com.oldvabik.internetshop.mapper.CategoryMapper;
import com.oldvabik.internetshop.model.Category;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.repository.CategoryRepository;
import com.oldvabik.internetshop.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryCache categoryCache;
    private final ProductRepository productRepository;

    private static final String CATEGORY_NOT_FOUND = "Category not found";

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryMapper categoryMapper,
                           CategoryCache categoryCache,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryCache = categoryCache;
        this.productRepository = productRepository;
    }

    public Category createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistsException("Category with name " + categoryDto.getName() + " already exists");
        }
        log.info("Creating new category: {}", categoryDto.getName());
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        categoryCache.put(savedCategory.getId(), savedCategory);
        log.info("Category with id {} created and cached", savedCategory.getId());
        return savedCategory;
    }

    public List<Category> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("Categories not found");
        }

        for (Category category : categories) {
            if (categoryCache.get(category.getId()) == null) {
                categoryCache.put(category.getId(), category);
                log.info("Category with id {} added to cache", category.getId());
            } else {
                log.info("Category with id {} already exists in cache", category.getId());
            }
        }
        return categories;
    }

    public Category getCategoryById(Long id) {
        Category cachedCategory = categoryCache.get(id);
        if (cachedCategory != null) {
            log.info("Category with id {} retrieved from cache", id);
            return cachedCategory;
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        categoryCache.put(category.getId(), category);
        log.info("Category with id {} retrieved from repository", category.getId());
        return category;
    }

    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            Optional<Category> foundCategory = categoryRepository.findByName(categoryDto.getName());
            if (foundCategory.isPresent()) {
                throw new AlreadyExistsException("Category with name " + categoryDto.getName() + " already exists");
            }
            category.setName(categoryDto.getName());
        }
        log.info("Updating Category with id {}", category.getId());
        Category savedCategory = categoryRepository.save(category);
        categoryCache.put(savedCategory.getId(), savedCategory);
        log.info("Category with id {} updated and cached", savedCategory.getId());
        return savedCategory;
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        log.warn("Deleting category with id {}", category.getId());
        categoryRepository.delete(category);
        categoryCache.remove(category.getId());
        log.info("Category with id {} deleted from cache", category.getId());
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Product with id " + categoryId + " not found");
        }
        return products;
    }

    public Product getProductById(Long categoryId, Long productId) {
        Product product = productRepository.findByIdAndCategoryId(productId, categoryId);
        if (product == null) {
            throw new ResourceNotFoundException("Product with id " + productId + " not found");
        }
        return product;
    }

}
