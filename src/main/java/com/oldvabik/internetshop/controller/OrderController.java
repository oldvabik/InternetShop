package com.oldvabik.internetshop.controller;

import com.oldvabik.internetshop.dto.OrderDto;
import com.oldvabik.internetshop.model.Order;
import com.oldvabik.internetshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
@Tag(name = "Order API", description = "Manage orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/users/{userId}/orders")
    @Operation(summary = "Create an order", description = "Creates a new order for a specific user")
    public Order createOrder(@PathVariable Long userId, @Valid @RequestBody OrderDto orderDto) {
        return orderService.createOrder(userId, orderDto);
    }

    @GetMapping("/orders")
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its unique identifier")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/users/{userId}/orders")
    @Operation(summary = "Get user orders", description = "Retrieves a list of orders for a specific user")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/users/{userId}/orders/{orderId}")
    @Operation(summary = "Get user order by ID", description = "Retrieves a specific order of a user by order ID")
    public Order getUserOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.getUserOrderById(userId, orderId);
    }

    @PutMapping("/users/{userId}/orders/{orderId}")
    @Operation(summary = "Update an order", description = "Updates an order's details for a specific user")
    public Order updateOrder(@PathVariable Long userId,
                                             @PathVariable Long orderId,
                                             @Valid @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(userId, orderId, orderDto);
    }

    @DeleteMapping("/users/{userId}/orders/{orderId}")
    @Operation(summary = "Delete an order", description = "Deletes a specific order of a user")
    public void deleteUserOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        orderService.deleteUserOrderById(userId, orderId);
    }

}
