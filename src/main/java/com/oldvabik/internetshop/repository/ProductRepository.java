package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByCategoryId(Long categoryId);

    Product findByIdAndCategoryId(Long id, Long categoryId);

}
