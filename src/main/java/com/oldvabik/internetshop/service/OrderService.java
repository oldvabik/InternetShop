package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.OrderCache;
import com.oldvabik.internetshop.dto.OrderDto;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCache orderCache;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderCache orderCache,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderCache = orderCache;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(Long id, OrderDto orderDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        Order order = new Order();
        Set<Product> products = new HashSet<>();
        Double totalPrice = 0.0;

        for (String productName : orderDto.getProductNames()) {
            Product product = productRepository.findByName(productName)
                    .orElseThrow(() -> new ResourceNotFoundException("Product with name " + productName + " not found"));
            products.add(product);
            totalPrice += product.getPrice();
        }

        order.setProducts(products);
        order.setTotalPrice(totalPrice);
        order.setUser(user);
        order.setDate(LocalDate.now());

        Order savedOrder = orderRepository.save(order);
        orderCache.put(savedOrder.getId(), savedOrder);
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithProducts();
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Orders not found");
        }

        for (Order order : orders) {
            if (orderCache.get(order.getId()) == null) {
                orderCache.put(order.getId(), order);
                log.info("Order with id {} added to cache", order.getId());
            } else {
                log.info("Order with id {} already exists", order.getId());
            }
        }
        return orders;
    }

    public Order getOrderById(Long id) {
        Order cachedOrder = orderCache.get(id);
        if (cachedOrder != null) {
            log.info("Order with id {} retrieved from cache", cachedOrder.getId());
            return cachedOrder;
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        orderCache.put(id, order);
        log.info("Order with id {} retrieved from repository and cached", order.getId());
        return order;
    }

    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findWithProductsByUserId(userId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Orders not found");
        }
        return orders;
    }

    public Order getUserOrderById(Long userId, Long orderId) {
        Order cachedOrder = orderCache.get(orderId);
        if (cachedOrder != null) {
            log.info("Order with id {} retrieved from cache", cachedOrder.getId());
            return cachedOrder;
        }

        Order order = orderRepository.findWithProductsByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }
        return order;
    }

    public Order updateOrder(Long userId, Long orderId, OrderDto orderDto) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }
        order.getProducts().clear();

        Set<Product> products = new HashSet<>();
        Double totalPrice = 0.0;

        for (int i = 0; i < orderDto.getProductNames().size(); i++) {
            Optional<Product> optionalProduct = productRepository.findByName(orderDto.getProductNames().get(i));
            if (optionalProduct.isEmpty()) {
                throw new ResourceNotFoundException("Product with name " + orderDto.getProductNames().get(i) + " not found");
            }
            Product product = optionalProduct.get();
            products.add(product);
            totalPrice += product.getPrice();
        }

        order.setProducts(products);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        orderCache.put(savedOrder.getId(), savedOrder);
        return order;
    }

    public void deleteUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }
        log.warn("Deleting order with id {}", order.getId());
        orderRepository.delete(order);
        orderCache.remove(order.getId());
        log.info("Order with id {} deleted from cache", order.getId());
    }

}
