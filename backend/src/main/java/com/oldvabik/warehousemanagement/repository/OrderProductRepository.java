package com.oldvabik.warehousemanagement.repository;

import com.oldvabik.warehousemanagement.model.OrderProduct;
import com.oldvabik.warehousemanagement.model.OrderProductId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {

    void deleteAllByOrderId(Long orderId);

    @Query("SELECT op FROM OrderProduct op JOIN FETCH op.product WHERE op.order.id = :orderId")
    List<OrderProduct> findByOrderIdWithProduct(@Param("orderId") Long orderId);

}
