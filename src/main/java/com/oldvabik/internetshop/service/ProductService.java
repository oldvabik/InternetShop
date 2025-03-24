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
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductCache productCache;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper,
                          ProductCache productCache) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.productCache = productCache;
    }

    public Product createProduct(ProductDto productDto) {
        Category category = categoryRepository.findByName(productDto.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with name " + productDto.getCategoryName() + " not found"
                ));

        Product product = productMapper.toEntity(productDto, category);
        Optional<Product> optionalProduct = productRepository.findByName(product.getName());
        if (optionalProduct.isPresent()) {
            throw new AlreadyExistsException("Product with name " + product.getName() + " already exists");
        }
        log.info("Creating new product: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        productCache.put(savedProduct.getId(), savedProduct);
        log.info("Product with id {} created and cached", savedProduct.getId());
        return savedProduct;
    }

    public List<Product> getProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Products not found");
        }

        for (Product product : products) {
            if (productCache.get(product.getId()) == null) {
                productCache.put(product.getId(), product);
                log.info("Product with id {} added to cache", product.getId());
            } else {
                log.info("Product with id {} already exists in cache", product.getId());
            }
        }
        return products;
    }

    public Product getProduct(Long id) {
        Product cachedProduct = productCache.get(id);
        if (cachedProduct != null) {
            log.info("Product with id {} retrieved from cache", cachedProduct.getId());
            return cachedProduct;
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " found"));
        productCache.put(id, product);
        log.info("Product with id {} retrieved from repository and cached", id);
        return product;
    }

    public Product updateProduct(Long id, @RequestBody ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (productDto.getName() != null && !productDto.getName().equals(product.getName())) {
            Optional<Product> foundProduct = productRepository.findByName(productDto.getName());
            if (foundProduct.isPresent()) {
                throw new AlreadyExistsException("Product with name " + productDto.getName() + " already exists");
            }
            product.setName(productDto.getName());
        }

        if (productDto.getPrice() != null && !productDto.getPrice().equals(product.getPrice())) {
            product.setPrice(productDto.getPrice());
        }

        if (productDto.getCategoryName() != null
                && (product.getCategory() == null
                || !productDto.getCategoryName().equals(product.getCategory().getName()))) {
            Optional<Category> optionalCategory = categoryRepository.findByName(productDto.getCategoryName());
            if (optionalCategory.isEmpty()) {
                throw new IllegalStateException(
                        String.format("Category with name %s does not exist", productDto.getCategoryName())
                );
            }
            product.setCategory(optionalCategory.get());
        }
        log.info("Product with id {}", product.getId());
        Product savedProduct = productRepository.save(product);
        productCache.put(savedProduct.getId(), savedProduct);
        log.info("Product with id {} updated and cached", savedProduct.getId());
        return savedProduct;
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        log.warn("Deleting product with id {}", product.getId());
        productRepository.delete(product);
        productCache.put(product.getId(), product);
        log.info("Product with id {} deleted from cache", product.getId());
    }

}
