package com.oldvabik.warehousemanagement.repository;

import com.oldvabik.warehousemanagement.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderProducts op LEFT JOIN FETCH op.product")
    List<Order> findAllWithProducts();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderProducts op LEFT JOIN FETCH op.product WHERE o.id = :id")
    Order findWithProductsById(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o "
            + "LEFT JOIN FETCH o.orderProducts op LEFT JOIN FETCH op.product WHERE o.user.id = :userId")
    List<Order> findWithProductsByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.orderProducts op LEFT JOIN FETCH op.product WHERE o.id = :id AND o.user.id = :userId")
    Order findWithProductsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    Order findByUserIdAndId(Long userId, Long orderId);

}
