package com.oldvabik.internetshop.controller;

import com.oldvabik.internetshop.dto.OrderDto;
import com.oldvabik.internetshop.model.Order;
import com.oldvabik.internetshop.service.OrderService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Long userId, @RequestBody OrderDto orderDto) {
        return orderService.createOrder(userId, orderDto);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<Order> getUserOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.getUserOrderById(userId, orderId);
    }

    @PutMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long userId,
                                             @PathVariable Long orderId,
                                             @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(userId, orderId, orderDto);
    }

    @DeleteMapping("/users/{userId}/orders/{orderId}")
    public void deleteUserOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        orderService.deleteUserOrderById(userId, orderId);
    }

}
