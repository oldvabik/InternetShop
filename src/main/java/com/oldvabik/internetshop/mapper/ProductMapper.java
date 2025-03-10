package com.oldvabik.internetshop.mapper;

import com.oldvabik.internetshop.dto.ProductDto;
import com.oldvabik.internetshop.model.Category;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.repository.CategoryRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setCategoryName(product.getCategory().getName());
        return productDto;
    }

    public Product toEntity(ProductDto productDto, CategoryRepository categoryRepository) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        if (productDto.getCategoryName() != null) {
            Optional<Category> optionalCategory = categoryRepository.findByName(productDto.getCategoryName());
            if (optionalCategory.isEmpty()) {
                throw new IllegalArgumentException("Category not found");
            }
            product.setCategory(optionalCategory.get());
        }
        return product;
    }

}
