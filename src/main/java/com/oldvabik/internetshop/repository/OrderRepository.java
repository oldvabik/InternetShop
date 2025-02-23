package com.oldvabik.internetshop.repository;

import com.oldvabik.internetshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
