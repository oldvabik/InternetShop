package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.dto.OrderDto;
import com.oldvabik.internetshop.model.Order;
import com.oldvabik.internetshop.model.Product;
import com.oldvabik.internetshop.model.User;
import com.oldvabik.internetshop.repository.OrderRepository;
import com.oldvabik.internetshop.repository.ProductRepository;
import com.oldvabik.internetshop.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<Order> createOrder(Long id, OrderDto orderDto) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Order order = new Order();
        Set<Product> products = new HashSet<>();
        Double totalPrice = 0.0;

        for (String productName : orderDto.getProductNames()) {
            Optional<Product> optionalProduct = productRepository.findByName(productName);
            if (optionalProduct.isEmpty()) {
                continue;
            }
            Product product = optionalProduct.get();
            products.add(product);
            totalPrice += product.getPrice();
        }

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        order.setProducts(products);
        order.setTotalPrice(totalPrice);
        order.setUser(user.get());
        order.setDate(LocalDate.now());

        return ResponseEntity.ok(orderRepository.save(order));
    }

    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithProducts();
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(orders);
    }

    public ResponseEntity<Order> getOrderById(Long id) {
        Order order = orderRepository.findWithProductsById(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(order);
    }

    public ResponseEntity<List<Order>> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findWithProductsByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(orders);
    }

    public ResponseEntity<Order> getUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findWithProductsByIdAndUserId(orderId, userId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(order);
    }

    public ResponseEntity<Order> updateOrder(Long userId, Long orderId, OrderDto orderDto) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        order.getProducts().clear();

        Set<Product> products = new HashSet<>();
        Double totalPrice = 0.0;

        for (int i = 0; i < orderDto.getProductNames().size(); i++) {
            Optional<Product> optionalProduct = productRepository.findByName(orderDto.getProductNames().get(i));
            if (optionalProduct.isEmpty()) {
                continue;
            }
            Product product = optionalProduct.get();
            products.add(product);
            totalPrice += product.getPrice();
        }

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        order.setProducts(products);
        order.setTotalPrice(totalPrice);

        return ResponseEntity.ok(orderRepository.save(order));
    }

    public void deleteUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            throw new IllegalStateException("Order not found");
        }
        orderRepository.delete(order);
    }

}
