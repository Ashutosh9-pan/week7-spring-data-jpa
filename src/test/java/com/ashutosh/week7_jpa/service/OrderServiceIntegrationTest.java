package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.OrderItem;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.OrderRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;
import com.ashutosh.week7_jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {

        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        Category category = new Category();
        category.setName("Test Electronics");
        category.setDescription("Integration test category");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Test Headphones");
        product.setDescription("Integration test product");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStockQuantity(10);
        product.setActive(true);
        product.setCategory(category);
        product = productRepository.save(product);

        user = new User();
        user.setName("Test User");
        user.setEmail("integration@example.com");
        user.setPassword("test123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user = userRepository.save(user);
    }

    @Test
    void shouldCreateOrderAndReduceStock() {

        Order order = buildOrder(2);

        Order savedOrder = orderService.createOrder(order);

        assertThat(savedOrder.getId()).isNotNull();

        assertThat(savedOrder.getTotalAmount())
                .isEqualByComparingTo("2000.00");

        Product updatedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertThat(updatedProduct.getStockQuantity())
                .isEqualTo(8);
    }

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() {

        Order order = buildOrder(50);

        assertThatThrownBy(() ->
                orderService.createOrder(order)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");

        Product unchangedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertThat(unchangedProduct.getStockQuantity())
                .isEqualTo(10);

        assertThat(orderRepository.count())
                .isZero();
    }

    private Order buildOrder(int quantity) {

        User orderUser = new User();
        orderUser.setId(user.getId());

        Product orderProduct = new Product();
        orderProduct.setId(product.getId());

        OrderItem item = new OrderItem();
        item.setProduct(orderProduct);
        item.setQuantity(quantity);

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        Order order = new Order();
        order.setUser(orderUser);
        order.setItems(items);

        return order;
    }
}