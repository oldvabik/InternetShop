package com.oldvabik.internetshop.controller;

import com.oldvabik.internetshop.dto.ProductDto;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.service.ProductService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "Manage products in the system")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a new product in the system")
    public Product createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates the details of an existing product")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
