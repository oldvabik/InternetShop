package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.ProductCache;
import com.oldvabik.internetshop.dto.ProductDto;
import com.oldvabik.internetshop.mapper.ProductMapper;
import com.oldvabik.internetshop.model.Category;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.repository.CategoryRepository;
import com.oldvabik.internetshop.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
        Optional<Category> optionalCategory = categoryRepository.findByName(productDto.getCategoryName());
        if (optionalCategory.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Category with name %s does not exist", productDto.getCategoryName())
            );
        }
        Product product = productMapper.toEntity(productDto, categoryRepository);
        Optional<Product> optionalProduct = productRepository.findByName(product.getName());
        if (optionalProduct.isPresent()) {
            throw new IllegalStateException(
                    String.format("Product with name %s already exists", product.getName())
            );
        }
        product = productRepository.save(product);
        productCache.put(product.getId(), product);
        return product;
    }

    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<Product> getProduct(Long id) {
        Product product = productCache.get(id);
        if (product == null) {
            product = productRepository.findById(id).orElse(null);
            if (product != null) {
                productCache.put(id, product);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.ok(product);
    }

    public Product updateProduct(Long id, @RequestBody ProductDto productDto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Product with id %s does not exist", id)
            );
        }

        Product product = optionalProduct.get();
        if (productDto.getName() != null && !productDto.getName().equals(product.getName())) {
            Optional<Product> foundProduct = productRepository.findByName(productDto.getName());
            if (foundProduct.isPresent()) {
                throw new IllegalStateException(
                        String.format("Product with name %s already exists", productDto.getName())
                );
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

        product = productRepository.save(product);
        productCache.put(product.getId(), product);
        return product;
    }

    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Product with id %s does not exist", id)
            );
        }
        productRepository.deleteById(id);
        productCache.remove(id);
    }

}
