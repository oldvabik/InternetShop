package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
