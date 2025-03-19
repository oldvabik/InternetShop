package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM products WHERE name = :name", nativeQuery = true)
    Optional<Product> findByName(String name);

    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId", nativeQuery = true)
    List<Product> findByCategoryId(Long categoryId);

    @Query(value = "SELECT * FROM products WHERE id = :id AND category_id = :categoryId", nativeQuery = true)
    Product findByIdAndCategoryId(Long id, Long categoryId);

}
