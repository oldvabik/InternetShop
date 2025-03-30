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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryCache categoryCache;
    @Mock
    private ProductRepository productRepository;

    @Test
    void createCategory_success() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");

        Category category = new Category();
        category.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");

        Mockito.when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);
        Mockito.when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(savedCategory);
        Mockito.doNothing().when(categoryCache).put(savedCategory.getId(), savedCategory);

        Category result = categoryService.createCategory(categoryDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Mockito.verify(categoryRepository).existsByName(categoryDto.getName());
        Mockito.verify(categoryMapper).toEntity(categoryDto);
        Mockito.verify(categoryRepository).save(category);
        Mockito.verify(categoryCache).put(savedCategory.getId(), savedCategory);
    }

    @Test
    void createCategory_throwsException() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");

        Mockito.when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);

        Assertions.assertThrows(AlreadyExistsException.class, () -> categoryService.createCategory(categoryDto));
        Mockito.verify(categoryRepository).existsByName(categoryDto.getName());
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryCache);
    }

    @Test
    void createCategories_success() {
        CategoryDto categoryDtoFirst = new CategoryDto();
        categoryDtoFirst.setName("Electronics");
        CategoryDto categoryDtoSecond = new CategoryDto();
        categoryDtoSecond.setName("Books");
        List<CategoryDto> categoriesDto = Arrays.asList(categoryDtoFirst, categoryDtoSecond);

        Mockito.when(categoryRepository.existsByName(categoryDtoFirst.getName())).thenReturn(false);
        Mockito.when(categoryRepository.existsByName(categoryDtoSecond.getName())).thenReturn(false);

        Category categoryFirst = new Category();
        categoryFirst.setName("Electronics");
        Category categorySecond = new Category();
        categorySecond.setName("Books");

        Mockito.when(categoryMapper.toEntity(categoryDtoFirst)).thenReturn(categoryFirst);
        Mockito.when(categoryMapper.toEntity(categoryDtoSecond)).thenReturn(categorySecond);

        Category savedCategoryFirst = new Category();
        savedCategoryFirst.setId(1L);
        savedCategoryFirst.setName("Electronics");
        Category savedCategorySecond = new Category();
        savedCategorySecond.setId(2L);
        savedCategorySecond.setName("Books");

        List<Category> savedCategories = Arrays.asList(savedCategoryFirst, savedCategorySecond);

        Mockito.when(categoryRepository.saveAll(Arrays.asList(categoryFirst, categorySecond))).thenReturn(savedCategories);

        List<Category> result = categoryService.createCategories(categoriesDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(categoryRepository).existsByName("Electronics");
        Mockito.verify(categoryRepository).existsByName("Books");
        Mockito.verify(categoryMapper).toEntity(categoryDtoFirst);
        Mockito.verify(categoryMapper).toEntity(categoryDtoSecond);
        Mockito.verify(categoryRepository).saveAll(Arrays.asList(categoryFirst, categorySecond));
    }

    @Test
    void createCategories_duplicateNames() {
        CategoryDto categoryDtoFirst = new CategoryDto();
        categoryDtoFirst.setName("Electronics");
        CategoryDto categoryDtoSecond = new CategoryDto();
        categoryDtoSecond.setName("Electronics");
        List<CategoryDto> categoriesDto = Arrays.asList(categoryDtoFirst, categoryDtoSecond);

        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.createCategories(categoriesDto));
    }

    @Test
    void createCategories_duplicateNamesInDb() {
        CategoryDto categoryDtoFirst = new CategoryDto();
        categoryDtoFirst.setName("Electronics");
        CategoryDto categoryDtoSecond = new CategoryDto();
        categoryDtoSecond.setName("Books");
        List<CategoryDto> categoriesDto = Arrays.asList(categoryDtoFirst, categoryDtoSecond);

        Mockito.when(categoryRepository.existsByName("Electronics")).thenReturn(true);
        Mockito.when(categoryRepository.existsByName("Books")).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.createCategories(categoriesDto));
    }

    @Test
    void getCategories_success() {
        Category categoryFirst = new Category();
        categoryFirst.setId(1L);
        categoryFirst.setName("Electronics");
        Category categorySecond = new Category();
        categorySecond.setId(2L);
        categorySecond.setName("Books");
        List<Category> categories = Arrays.asList(categoryFirst, categorySecond);

        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        Mockito.when(categoryCache.get(1L)).thenReturn(null);
        Mockito.when(categoryCache.get(2L)).thenReturn(categorySecond);
        Mockito.doNothing().when(categoryCache).put(1L, categoryFirst);

        List<Category> result = categoryService.getCategories();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(categoryRepository).findAll();
        Mockito.verify(categoryCache).get(1L);
        Mockito.verify(categoryCache).get(2L);
        Mockito.verify(categoryCache).put(1L, categoryFirst);
    }

    @Test
    void getCategories_throwsException() {
        Mockito.when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategories());
    }

    @Test
    void testGetCategoryById_fromCache() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Mockito.when(categoryCache.get(1L)).thenReturn(category);

        Category result = categoryService.getCategoryById(1L);

        Assertions.assertEquals(category, result);
        Mockito.verify(categoryCache).get(1L);
        Mockito.verify(categoryRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getCategoryById_fromRepository() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Mockito.when(categoryCache.get(1L)).thenReturn(null);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.doNothing().when(categoryCache).put(1L, category);

        Category result = categoryService.getCategoryById(1L);

        Assertions.assertEquals(category, result);
        Mockito.verify(categoryCache).get(1L);
        Mockito.verify(categoryRepository).findById(1L);
        Mockito.verify(categoryCache).put(1L, category);
    }

    @Test
    void getCategoryById_throwsException() {
        Mockito.when(categoryCache.get(1L)).thenReturn(null);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void updateCategory_success() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        CategoryDto updateDto = new CategoryDto();
        updateDto.setName("Home Appliances");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Home Appliances");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        Mockito.when(categoryRepository.findByName(updateDto.getName())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
        Mockito.doNothing().when(categoryCache).put(1L, updatedCategory);

        Category result = categoryService.updateCategory(1L, updateDto);

        Assertions.assertEquals("Home Appliances", result.getName());
        Mockito.verify(categoryRepository).findById(1L);
        Mockito.verify(categoryRepository).findByName(updateDto.getName());
        Mockito.verify(categoryRepository).save(existingCategory);
        Mockito.verify(categoryCache).put(1L, updatedCategory);
    }

    @Test
    void updateCategory_notFound() {
        CategoryDto updateDto = new CategoryDto();
        updateDto.setName("Home Appliances");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, updateDto));
    }

    @Test
    void updateCategory_duplicateName() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        CategoryDto updateDto = new CategoryDto();
        updateDto.setName("Books");

        Category anotherCategory = new Category();
        anotherCategory.setId(2L);
        anotherCategory.setName("Books");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        Mockito.when(categoryRepository.findByName(updateDto.getName())).thenReturn(Optional.of(anotherCategory));

        Assertions.assertThrows(AlreadyExistsException.class, () -> categoryService.updateCategory(1L, updateDto));
    }

    @Test
    void deleteCategoryById_success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.doNothing().when(categoryCache).remove(1L);

        categoryService.deleteCategoryById(1L);

        Mockito.verify(categoryRepository).findById(1L);
        Mockito.verify(categoryRepository).delete(category);
        Mockito.verify(categoryCache).remove(1L);
    }

    @Test
    void deleteCategoryById_throwsException() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(1L));
    }

    @Test
    void getProductsByCategory_success() {
        Product productFirst = new Product();
        productFirst.setId(1L);
        productFirst.setName("Product 1");

        Product productSecond = new Product();
        productSecond.setId(2L);
        productSecond.setName("Product 2");

        List<Product> products = Arrays.asList(productFirst, productSecond);

        Mockito.when(productRepository.findByCategoryId(1L)).thenReturn(products);

        List<Product> result = categoryService.getProductsByCategory(1L);

        Assertions.assertEquals(2, result.size());
        Mockito.verify(productRepository).findByCategoryId(1L);
    }

    @Test
    void getProductsByCategory_throwsException() {
        Mockito.when(productRepository.findByCategoryId(1L)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getProductsByCategory(1L));
    }

    @Test
    void getProductById_success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        Mockito.when(productRepository.findByIdAndCategoryId(1L, 1L)).thenReturn(product);

        Product result = categoryService.getProductById(1L, 1L);

        Assertions.assertEquals(product, result);
        Mockito.verify(productRepository).findByIdAndCategoryId(1L, 1L);
    }

    @Test
    void testGetProductById_throwsException() {
        Mockito.when(productRepository.findByIdAndCategoryId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getProductById(1L, 1L));
    }

}
