package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products")
    List<Order> findAllWithProducts();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.id = :id")
    Order findWithProductsById(@Param("id") Long id);

    // SELECT o.*, p.* FROM orders o
    // LEFT JOIN order_product op ON o.id = op.order_id
    // LEFT JOIN products p ON op.product_id = p.id
    // WHERE o.user_id = :userId (native query)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.user.id = :userId")
    List<Order> findWithProductsByUserId(@Param("userId") Long userId);

    // SELECT o.*, p.* FROM orders o
    // LEFT JOIN order_product op ON o.id = op.order_id
    // LEFT JOIN products p ON op.product_id = p.id
    // WHERE o.id = :id AND o.user_id = :userId (native query)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.id = :id AND o.user.id = :userId")
    Order findWithProductsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    Order findByUserIdAndId(Long userId, Long orderId);

}
