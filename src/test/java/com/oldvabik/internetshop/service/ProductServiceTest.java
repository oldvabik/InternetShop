package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.ProductCache;
import com.oldvabik.internetshop.dto.ProductDto;
import com.oldvabik.internetshop.exception.AlreadyExistsException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import com.oldvabik.internetshop.mapper.ProductMapper;
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
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductCache productCache;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createProduct_success() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setCategoryName("Electronics");
        productDto.setPrice(1500.0);

        Category category = new Category();
        category.setName("Electronics");

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(1500.0);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Laptop");
        savedProduct.setPrice(1500.0);
        savedProduct.setCategory(category);

        Mockito.when(categoryRepository.findByName(productDto.getCategoryName())).thenReturn(Optional.of(category));
        Mockito.when(productMapper.toEntity(productDto, category)).thenReturn(product);
        Mockito.when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        Mockito.when(productRepository.save(product)).thenReturn(savedProduct);
        Mockito.doNothing().when(productCache).put(savedProduct.getId(), savedProduct);

        Product result = productService.createProduct(productDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Laptop", result.getName());
        Mockito.verify(categoryRepository).findByName(productDto.getCategoryName());
        Mockito.verify(productMapper).toEntity(productDto, category);
        Mockito.verify(productRepository).findByName(product.getName());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(productCache).put(savedProduct.getId(), savedProduct);
    }

    @Test
    void createProduct_categoryNotFound() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setCategoryName("Electronics");

        Mockito.when(categoryRepository.findByName(productDto.getCategoryName())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productDto));
        Mockito.verify(categoryRepository).findByName(productDto.getCategoryName());
        Mockito.verifyNoInteractions(productMapper, productRepository, productCache);
    }

    @Test
    void createProduct_alreadyExists() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setCategoryName("Electronics");

        Category category = new Category();
        category.setName("Electronics");

        Product product = new Product();
        product.setName("Laptop");
        product.setCategory(category);

        Mockito.when(categoryRepository.findByName(productDto.getCategoryName())).thenReturn(Optional.of(category));
        Mockito.when(productMapper.toEntity(productDto, category)).thenReturn(product);
        Mockito.when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));

        Assertions.assertThrows(AlreadyExistsException.class, () -> productService.createProduct(productDto));
        Mockito.verify(categoryRepository).findByName(productDto.getCategoryName());
        Mockito.verify(productMapper).toEntity(productDto, category);
        Mockito.verify(productRepository).findByName(product.getName());
    }

    @Test
    void createProducts_success() {
        ProductDto productDtoFirst = new ProductDto();
        productDtoFirst.setName("Laptop");
        productDtoFirst.setCategoryName("Electronics");
        productDtoFirst.setPrice(1500.0);

        ProductDto productDtoSecond = new ProductDto();
        productDtoSecond.setName("Book");
        productDtoSecond.setCategoryName("Books");
        productDtoSecond.setPrice(25.0);

        List<ProductDto> productsDto = Arrays.asList(productDtoFirst, productDtoSecond);

        Category categoryElectronics = new Category();
        categoryElectronics.setName("Electronics");

        Category categoryBooks = new Category();
        categoryBooks.setName("Books");

        Product productFirst = new Product();
        productFirst.setName("Laptop");
        productFirst.setPrice(1500.0);
        productFirst.setCategory(categoryElectronics);

        Product productSecond = new Product();
        productSecond.setName("Book");
        productSecond.setPrice(25.0);
        productSecond.setCategory(categoryBooks);

        Mockito.when(productRepository.existsByName(productDtoFirst.getName())).thenReturn(false);
        Mockito.when(productRepository.existsByName(productDtoSecond.getName())).thenReturn(false);
        Mockito.when(categoryRepository.findByName(productDtoFirst.getCategoryName())).thenReturn(Optional.of(categoryElectronics));
        Mockito.when(categoryRepository.findByName(productDtoSecond.getCategoryName())).thenReturn(Optional.of(categoryBooks));
        Mockito.when(productMapper.toEntity(productDtoFirst, categoryElectronics)).thenReturn(productFirst);
        Mockito.when(productMapper.toEntity(productDtoSecond, categoryBooks)).thenReturn(productSecond);

        List<Product> savedProducts = Arrays.asList(
                new Product() {{
                    setId(1L);
                    setName("Laptop");
                    setPrice(1500.0);
                    setCategory(categoryElectronics);
                }},
                new Product() {{
                    setId(2L);
                    setName("Book");
                    setPrice(25.0);
                    setCategory(categoryBooks);
                }}
        );
        Mockito.when(productRepository.saveAll(Arrays.asList(productFirst, productSecond))).thenReturn(savedProducts);

        List<Product> result = productService.createProducts(productsDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(productRepository).existsByName("Laptop");
        Mockito.verify(productRepository).existsByName("Book");
        Mockito.verify(categoryRepository).findByName("Electronics");
        Mockito.verify(categoryRepository).findByName("Books");
        Mockito.verify(productMapper).toEntity(productDtoFirst, categoryElectronics);
        Mockito.verify(productMapper).toEntity(productDtoSecond, categoryBooks);
        Mockito.verify(productRepository).saveAll(Arrays.asList(productFirst, productSecond));
    }

    @Test
    void createProducts_duplicateNamesInRequest() {
        ProductDto productDtoFirst = new ProductDto();
        productDtoFirst.setName("Laptop");
        productDtoFirst.setCategoryName("Electronics");

        ProductDto productDtoSecond = new ProductDto();
        productDtoSecond.setName("Laptop");
        productDtoSecond.setCategoryName("Electronics");

        List<ProductDto> productsDto = Arrays.asList(productDtoFirst, productDtoSecond);

        Assertions.assertThrows(IllegalArgumentException.class, () -> productService.createProducts(productsDto));
    }

    @Test
    void createProducts_duplicateNamesInDb() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setCategoryName("Electronics");

        List<ProductDto> productsDto = Collections.singletonList(productDto);

        Mockito.when(productRepository.existsByName("Laptop")).thenReturn(true);

        Assertions.assertThrows(AlreadyExistsException.class, () -> productService.createProducts(productsDto));
    }

    @Test
    void createProducts_categoryNotFound() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setCategoryName("NonExistingCategory");

        List<ProductDto> productsDto = Collections.singletonList(productDto);

        Mockito.when(productRepository.existsByName("Laptop")).thenReturn(false);
        Mockito.when(categoryRepository.findByName("NonExistingCategory")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.createProducts(productsDto));
    }

    @Test
    void getProducts_success() {
        Category category = new Category();
        category.setName("Electronics");

        Product productFirst = new Product();
        productFirst.setId(1L);
        productFirst.setName("Laptop");
        productFirst.setCategory(category);

        Product productSecond = new Product();
        productSecond.setId(2L);
        productSecond.setName("Book");
        productSecond.setCategory(category);

        List<Product> products = Arrays.asList(productFirst, productSecond);

        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(productCache.get(1L)).thenReturn(null);
        Mockito.when(productCache.get(2L)).thenReturn(productSecond);
        Mockito.doNothing().when(productCache).put(1L, productFirst);

        List<Product> result = productService.getProducts();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(productRepository).findAll();
        Mockito.verify(productCache).get(1L);
        Mockito.verify(productCache).get(2L);
        Mockito.verify(productCache).put(1L, productFirst);
    }

    @Test
    void getProducts_throwsException() {
        Mockito.when(productRepository.findAll()).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProducts());
    }

    @Test
    void getProduct_fromCache() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        Mockito.when(productCache.get(1L)).thenReturn(product);

        Product result = productService.getProduct(1L);

        Assertions.assertEquals(product, result);
        Mockito.verify(productCache).get(1L);
        Mockito.verify(productRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getProduct_fromRepository() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        Mockito.when(productCache.get(1L)).thenReturn(null);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productCache).put(1L, product);

        Product result = productService.getProduct(1L);

        Assertions.assertEquals(product, result);
        Mockito.verify(productCache).get(1L);
        Mockito.verify(productRepository).findById(1L);
        Mockito.verify(productCache).put(1L, product);
    }

    @Test
    void getProduct_throwsException() {
        Mockito.when(productCache.get(1L)).thenReturn(null);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(1L));
    }

    @Test
    void updateProduct_success() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");
        existingProduct.setPrice(1500.0);
        Category oldCategory = new Category();
        oldCategory.setName("Electronics");
        existingProduct.setCategory(oldCategory);

        ProductDto updateDto = new ProductDto();
        updateDto.setName("Gaming Laptop");
        updateDto.setPrice(2000.0);
        updateDto.setCategoryName("Gaming");

        Category newCategory = new Category();
        newCategory.setName("Gaming");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Gaming Laptop");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setCategory(newCategory);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        Mockito.when(productRepository.findByName(updateDto.getName())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.findByName(updateDto.getCategoryName())).thenReturn(Optional.of(newCategory));
        Mockito.when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        Mockito.doNothing().when(productCache).put(1L, updatedProduct);

        Product result = productService.updateProduct(1L, updateDto);

        Assertions.assertEquals("Gaming Laptop", result.getName());
        Assertions.assertEquals(2000.0, result.getPrice());
        Assertions.assertEquals("Gaming", result.getCategory().getName());
        Mockito.verify(productRepository).findById(1L);
        Mockito.verify(productRepository).findByName(updateDto.getName());
        Mockito.verify(categoryRepository).findByName(updateDto.getCategoryName());
        Mockito.verify(productRepository).save(existingProduct);
        Mockito.verify(productCache).put(1L, updatedProduct);
    }

    @Test
    void updateProduct_notFound() {
        ProductDto updateDto = new ProductDto();
        updateDto.setName("Gaming Laptop");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateDto));
    }

    @Test
    void updateProduct_duplicateName() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");

        ProductDto updateDto = new ProductDto();
        updateDto.setName("Smartphone");

        Product anotherProduct = new Product();
        anotherProduct.setId(2L);
        anotherProduct.setName("Smartphone");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        Mockito.when(productRepository.findByName(updateDto.getName())).thenReturn(Optional.of(anotherProduct));

        Assertions.assertThrows(AlreadyExistsException.class, () -> productService.updateProduct(1L, updateDto));
    }

    @Test
    void updateProduct_categoryNotFound() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");
        existingProduct.setPrice(1500.0);
        Category oldCategory = new Category();
        oldCategory.setName("Electronics");
        existingProduct.setCategory(oldCategory);

        ProductDto updateDto = new ProductDto();
        updateDto.setCategoryName("NonExistingCategory");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        Mockito.when(categoryRepository.findByName(updateDto.getCategoryName())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateDto));
    }

    @Test
    void deleteProduct_success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productCache).remove(1L);
        Mockito.doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        Mockito.verify(productRepository).findById(1L);
        Mockito.verify(productCache).remove(1L);
        Mockito.verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_throwsException() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

}
