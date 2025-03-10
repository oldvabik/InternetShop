package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products")
    List<Order> findAllWithProducts();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.id = :id")
    Order findWithProductsById(Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.user.id = :userId")
    List<Order> findWithProductsByUserId(Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.id = :id AND o.user.id = :userId")
    Order findWithProductsByIdAndUserId(Long id, Long userId);

    Order findByUserIdAndId(Long userId, Long orderId);

}
