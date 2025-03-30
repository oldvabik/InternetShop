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
        Long userId = 1L;
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Arrays.asList("Laptop", "Book"));

        User user = new User();
        user.setId(userId);

        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setPrice(1500.0);

        Product book = new Product();
        book.setName("Book");
        book.setPrice(25.0);

        // Стабы для поиска пользователя и продуктов
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findByName("Laptop")).thenReturn(Optional.of(laptop));
        Mockito.when(productRepository.findByName("Book")).thenReturn(Optional.of(book));

        // При сохранении заказа возвращается заказ с присвоенным id и рассчитанной суммой
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

        Order result = orderService.createOrder(userId, orderDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(1525.0, result.getTotalPrice());
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(productRepository).findByName("Laptop");
        Mockito.verify(productRepository).findByName("Book");
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderCache).put(savedOrder.getId(), savedOrder);
    }

    // Тест для createOrder - пользователь не найден
    @Test
    void createOrder_userNotFound() {
        Long userId = 1L;
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Laptop"));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(userId, orderDto));
        Mockito.verify(userRepository).findById(userId);
    }

    // Тест для createOrder - продукт не найден
    @Test
    void createOrder_productNotFound() {
        Long userId = 1L;
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Laptop"));

        User user = new User();
        user.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(userId, orderDto));
    }

    // Тест для getAllOrders - успешное получение списка заказов
    @Test
    void getAllOrders_success() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = Arrays.asList(order1, order2);

        Mockito.when(orderRepository.findAllWithProducts()).thenReturn(orders);
        // Предположим, что order1 отсутствует в кэше, а order2 уже там
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

    // Тест для getAllOrders - список заказов пуст
    @Test
    void getAllOrders_empty_throwsException() {
        Mockito.when(orderRepository.findAllWithProducts()).thenReturn(Collections.emptyList());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getAllOrders());
    }

    // Тест для getOrderById - получение заказа из кэша
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

    // Тест для getOrderById - получение заказа из репозитория
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

    // Тест для getOrderById - заказ не найден
    @Test
    void getOrderById_notFound() {
        Mockito.when(orderCache.get(1L)).thenReturn(null);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    // Тест для getUserOrders - успешное получение заказов пользователя
    @Test
    void getUserOrders_success() {
        Long userId = 1L;
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = Arrays.asList(order1, order2);

        Mockito.when(orderRepository.findWithProductsByUserId(userId)).thenReturn(orders);
        List<Order> result = orderService.getUserOrders(userId);

        Assertions.assertEquals(2, result.size());
        Mockito.verify(orderRepository).findWithProductsByUserId(userId);
    }

    // Тест для getUserOrders - заказы пользователя отсутствуют
    @Test
    void getUserOrders_empty_throwsException() {
        Long userId = 1L;
        Mockito.when(orderRepository.findWithProductsByUserId(userId)).thenReturn(Collections.emptyList());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getUserOrders(userId));
    }

    // Тест для getUserOrderById - получение заказа из кэша
    @Test
    void getUserOrderById_fromCache() {
        Long userId = 1L;
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        Mockito.when(orderCache.get(orderId)).thenReturn(order);

        Order result = orderService.getUserOrderById(userId, orderId);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(orderId);
        Mockito.verify(orderRepository, Mockito.never()).findWithProductsByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    }

    // Тест для getUserOrderById - получение заказа из репозитория
    @Test
    void getUserOrderById_fromRepository() {
        Long userId = 1L;
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        Mockito.when(orderCache.get(orderId)).thenReturn(null);
        Mockito.when(orderRepository.findWithProductsByIdAndUserId(orderId, userId)).thenReturn(order);

        Order result = orderService.getUserOrderById(userId, orderId);

        Assertions.assertEquals(order, result);
        Mockito.verify(orderCache).get(orderId);
        Mockito.verify(orderRepository).findWithProductsByIdAndUserId(orderId, userId);
    }

    // Тест для getUserOrderById - заказ не найден
    @Test
    void getUserOrderById_notFound() {
        Long userId = 1L;
        Long orderId = 1L;
        Mockito.when(orderCache.get(orderId)).thenReturn(null);
        Mockito.when(orderRepository.findWithProductsByIdAndUserId(orderId, userId)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getUserOrderById(userId, orderId));
    }

    // Тест для updateOrder - успешное обновление заказа
    @Test
    void updateOrder_success() {
        Long userId = 1L;
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        // Исходный заказ содержит один продукт
        Product oldProduct = new Product();
        oldProduct.setName("Laptop");
        oldProduct.setPrice(1500.0);
        order.setProducts(new HashSet<>(Collections.singletonList(oldProduct)));
        order.setTotalPrice(1500.0);
        order.setUser(new User());

        // Обновляем заказ: новый список продуктов
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Book"));

        Product newProduct = new Product();
        newProduct.setName("Book");
        newProduct.setPrice(25.0);

        Mockito.when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(order);
        Mockito.when(productRepository.findByName("Book")).thenReturn(Optional.of(newProduct));

        // Ожидаемый обновлённый заказ
        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setProducts(new HashSet<>(Collections.singletonList(newProduct)));
        updatedOrder.setTotalPrice(25.0);
        updatedOrder.setUser(order.getUser());
        updatedOrder.setDate(order.getDate());

        Mockito.when(orderRepository.save(order)).thenReturn(updatedOrder);
        Mockito.doNothing().when(orderCache).put(orderId, updatedOrder);

        Order result = orderService.updateOrder(userId, orderId, orderDto);

        Assertions.assertEquals(25.0, result.getTotalPrice());
        Assertions.assertTrue(result.getProducts().contains(newProduct));
        Mockito.verify(orderRepository).findByUserIdAndId(userId, orderId);
        Mockito.verify(productRepository).findByName("Book");
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(orderCache).put(orderId, updatedOrder);
    }

    // Тест для updateOrder - заказ не найден
    @Test
    void updateOrder_orderNotFound() {
        Long userId = 1L;
        Long orderId = 1L;
        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("Book"));

        Mockito.when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(userId, orderId, orderDto));
    }

    // Тест для updateOrder - продукт не найден при обновлении
    @Test
    void updateOrder_productNotFound() {
        Long userId = 1L;
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        // Исходный заказ содержит один продукт
        Product oldProduct = new Product();
        oldProduct.setName("Laptop");
        oldProduct.setPrice(1500.0);
        order.setProducts(new HashSet<>(Collections.singletonList(oldProduct)));
        order.setTotalPrice(1500.0);
        order.setUser(new User());

        OrderDto orderDto = new OrderDto();
        orderDto.setProductNames(Collections.singletonList("NonExistingProduct"));

        Mockito.when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(order);
        Mockito.when(productRepository.findByName("NonExistingProduct")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(userId, orderId, orderDto));
    }

    @Test
    void deleteUserOrderById_success() {
        Long userId = 1L;
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        Mockito.when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(order);
        Mockito.doNothing().when(orderCache).remove(orderId);
        Mockito.doNothing().when(orderRepository).delete(order);

        orderService.deleteUserOrderById(userId, orderId);

        Mockito.verify(orderRepository).findByUserIdAndId(userId, orderId);
        Mockito.verify(orderRepository).delete(order);
        Mockito.verify(orderCache).remove(orderId);
    }

    @Test
    void deleteUserOrderById_notFound() {
        Mockito.when(orderRepository.findByUserIdAndId(1L, 1L)).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.deleteUserOrderById(1L, 1L));
    }

}
