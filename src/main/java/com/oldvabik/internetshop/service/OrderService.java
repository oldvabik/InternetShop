package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.cache.OrderCache;
import com.oldvabik.internetshop.dto.OrderDto;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import com.oldvabik.internetshop.model.*;
import com.oldvabik.internetshop.repository.OrderProductRepository;
import com.oldvabik.internetshop.repository.OrderRepository;
import com.oldvabik.internetshop.repository.ProductRepository;
import com.oldvabik.internetshop.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCache orderCache;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    private static final String ORDER_NOT_FOUND = "Order not found";

    public OrderService(OrderRepository orderRepository,
                        OrderCache orderCache,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.orderCache = orderCache;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public Order createOrder(Long id, OrderDto orderDto) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDate(LocalDate.now());
        order.setTotalPrice(0.0);
        Order savedOrder = orderRepository.save(order);

        Double totalPrice = 0.0;
        Set<OrderProduct> orderProducts = new HashSet<>();

        for (OrderDto.OrderProductRequest item : orderDto.getItems()) {
            Product product = productRepository.findByName(item.getProductName())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getQuantity() < item.getQuantity()) {
                throw new ResourceNotFoundException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setId(new OrderProductId(savedOrder.getId(), product.getId()));
            orderProduct.setOrder(savedOrder);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            orderProductRepository.save(orderProduct);
            orderProducts.add(orderProduct);

            totalPrice += product.getPrice() * item.getQuantity();
        }

        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setOrderProducts(orderProducts);

        log.info("Creating order with user id {}", order.getUser().getId());
        Order finalOrder = orderRepository.save(savedOrder);
        orderCache.put(finalOrder.getId(), finalOrder);
        log.info("Order with id {} created and cached", savedOrder.getId());
        return finalOrder;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithProducts();
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
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
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));
        orderCache.put(id, order);
        log.info("Order with id {} retrieved from repository and cached", order.getId());
        return order;
    }

    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findWithProductsByUserId(userId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
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
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
        }
        return order;
    }

    @Transactional
    public Order updateOrder(Long userId, Long orderId, OrderDto orderDto) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
        }

        for (OrderProduct op : order.getOrderProducts()) {
            Product product = op.getProduct();
            product.setQuantity(product.getQuantity() + op.getQuantity());
            productRepository.save(product);
        }

        orderProductRepository.deleteAllByOrderId(orderId);
        order.getOrderProducts().clear();

        Double totalPrice = 0.0;
        Set<OrderProduct> orderProducts = new HashSet<>();

        for (OrderDto.OrderProductRequest item : orderDto.getItems()) {
            Product product = productRepository.findByName(item.getProductName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product with name " + item.getProductName() + " not found"));

            if (product.getQuantity() < item.getQuantity()) {
                throw new ResourceNotFoundException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setId(new OrderProductId(orderId, product.getId()));
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            orderProductRepository.save(orderProduct);
            orderProducts.add(orderProduct);

            totalPrice += product.getPrice() * item.getQuantity();
        }

        order.setOrderProducts(orderProducts);
        order.setTotalPrice(totalPrice);

        log.info("Update order with id {}", order.getId());
        Order savedOrder = orderRepository.save(order);
        orderCache.put(savedOrder.getId(), savedOrder);
        log.info("Order with id {} updated and cached", order.getId());
        return savedOrder;
    }

    @Transactional
    public void deleteUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND);
        }
        log.warn("Deleting order with id {}", order.getId());
        orderProductRepository.deleteAllByOrderId(orderId);
        orderRepository.delete(order);
        orderCache.remove(order.getId());
        log.info("Order with id {} deleted from cache", order.getId());
    }

}
