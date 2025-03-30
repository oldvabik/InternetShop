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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderCache orderCache;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @Test
    void createOrder_success() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Arrays.asList("Laptop", "Book"));

        User user = new User();
        user.setId(1L);

        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setPrice(1500.0);

        Product book = new Product();
        book.setName("Book");
        book.setPrice(25.0);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findByName("Laptop")).thenReturn(Optional.of(laptop));
        Mockito.when(productRepository.findByName("Book")).thenReturn(Optional.of(book));

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new HashSet<>(Arrays.asList(laptop, book)));
        order.setTotalPrice(1525.0);
        order.setDate(LocalDate.now());

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUser(user);
        savedOrder.setProducts(order.getProducts());
        savedOrder.setTotalPrice(1525.0);
        savedOrder.setDate(order.getDate());

        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(savedOrder);
        Mockito.doNothing().when(orderCache).put(savedOrder.getId(), savedOrder);

        Order result = orderService.createOrder(1L, orderDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(1525.0, result.getTotalPrice());
        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(productRepository).findByName("Laptop");
        Mockito.verify(productRepository).findByName("Book");
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderCache).put(savedOrder.getId(), savedOrder);
    }

    @Test
    void createOrder_userNotFound() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Laptop"));

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(1L, orderDto));
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    void createOrder_productNotFound() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Laptop"));

        User user = new User();
        user.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(1L, orderDto));
    }

    @Test
    void getAllOrders_success() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = Arrays.asList(order1, order2);

        Mockito.when(orderRepository.findAllWithProducts()).thenReturn(orders);
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderCache.get(2L)).thenReturn(order2);
        Mockito.doNothing().when(orderCache).put(1L, order1);

        List<Order> result = orderService.getAllOrders();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(orderRepository).findAllWithProducts();
        Mockito.verify(orderCache).get(1L);
        Mockito.verify(orderCache).get(2L);
        Mockito.verify(orderCache).put(1L, order1);
    }

    @Test
    void getAllOrders_throwsException() {
        Mockito.when(orderRepository.findAllWithProducts()).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getAllOrders());
    }

    @Test
    void getOrderById_fromCache() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderCache.get(1L)).thenReturn(order);

        Order result = orderService.getOrderById(1L);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(1L);
        Mockito.verify(orderRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getOrderById_fromRepository() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.doNothing().when(orderCache).put(1L, order);

        Order result = orderService.getOrderById(1L);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(1L);
        Mockito.verify(orderRepository).findById(1L);
        Mockito.verify(orderCache).put(1L, order);
    }

    @Test
    void getOrderById_notFound() {
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getUserOrders_success() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = Arrays.asList(order1, order2);

        Mockito.when(orderRepository.findWithProductsByUserId(1L)).thenReturn(orders);
        List<Order> result = orderService.getUserOrders(1L);

        Assertions.assertEquals(2, result.size());
        Mockito.verify(orderRepository).findWithProductsByUserId(1L);
    }

    @Test
    void getUserOrders_throwsException() {
        Mockito.when(orderRepository.findWithProductsByUserId(1L)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getUserOrders(1L));
    }

    @Test
    void getUserOrderById_fromCache() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderCache.get(1L)).thenReturn(order);

        Order result = orderService.getUserOrderById(1L, 1L);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(1L);
        Mockito.verify(orderRepository, Mockito.never()).findWithProductsByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getUserOrderById_fromRepository() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderRepository.findWithProductsByIdAndUserId(1L, 1L)).thenReturn(order);

        Order result = orderService.getUserOrderById(1L, 1L);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(1L);
        Mockito.verify(orderRepository).findWithProductsByIdAndUserId(1L, 1L);
    }

    @Test
    void getUserOrderById_notFound() {
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderRepository.findWithProductsByIdAndUserId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getUserOrderById(1L, 1L));
    }

    @Test
    void updateOrder_success() {
        Order order = new Order();
        order.setId(1L);
        Product oldProduct = new Product();
        oldProduct.setName("Laptop");
        oldProduct.setPrice(1500.0);
        order.setProducts(new HashSet<>(Collections.singletonList(oldProduct)));
        order.setTotalPrice(1500.0);
        order.setUser(new User());

        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Book"));

        Product newProduct = new Product();
        newProduct.setName("Book");
        newProduct.setPrice(25.0);

        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(order);
        Mockito.when(productRepository.findByName("Book")).thenReturn(Optional.of(newProduct));

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setProducts(new HashSet<>(Collections.singletonList(newProduct)));
        updatedOrder.setTotalPrice(25.0);
        updatedOrder.setUser(order.getUser());
        updatedOrder.setDate(order.getDate());

        Mockito.when(orderRepository.save(order)).thenReturn(updatedOrder);
        Mockito.doNothing().when(orderCache).put(1L, updatedOrder);

        Order result = orderService.updateOrder(1L, 1L, orderDto);

        Assertions.assertEquals(25.0, result.getTotalPrice());
        Assertions.assertTrue(result.getProducts().contains(newProduct));
        Mockito.verify(orderRepository).findByUserIdAndId(1L, 1L);
        Mockito.verify(productRepository).findByName("Book");
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(orderCache).put(1L, updatedOrder);
    }

    @Test
    void updateOrder_orderNotFound() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Book"));

        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, 1L, orderDto));
    }

    @Test
    void updateOrder_productNotFound() {
        Order order = new Order();
        order.setId(1L);
        Product oldProduct = new Product();
        oldProduct.setName("Laptop");
        oldProduct.setPrice(1500.0);
        order.setProducts(new HashSet<>(Collections.singletonList(oldProduct)));
        order.setTotalPrice(1500.0);
        order.setUser(new User());

        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("NonExistingProduct"));

        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(order);
        Mockito.when(productRepository.findByName("NonExistingProduct")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, 1L, orderDto));
    }

    @Test
    void deleteUserOrderById_success() {
        Order order = new Order();
        order.setId(1L);

        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(order);
        Mockito.doNothing().when(orderCache).remove(1L);
        Mockito.doNothing().when(orderRepository).delete(order);

        orderService.deleteUserOrderById(1L, 1L);

        Mockito.verify(orderRepository).findByUserIdAndId(1L, 1L);
        Mockito.verify(orderRepository).delete(order);
        Mockito.verify(orderCache).remove(1L);
    }

    @Test
    void deleteUserOrderById_notFound() {
        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.deleteUserOrderById(1L, 1L));
    }

}
