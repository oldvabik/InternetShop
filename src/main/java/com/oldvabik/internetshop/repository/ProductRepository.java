package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
