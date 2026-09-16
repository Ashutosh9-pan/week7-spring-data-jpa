package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.dto.DailyOrderReport;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class DailyOrderReportIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {

        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        Category category = new Category();
        category.setName("Report Test Category");
        category.setDescription("Category for report tests");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Report Test Product");
        product.setDescription("Product for report tests");
        product.setPrice(new BigDecimal("1200.00"));
        product.setStockQuantity(20);
        product.setActive(true);
        product.setCategory(category);
        product = productRepository.save(product);

        user = new User();
        user.setName("Report Test User");
        user.setEmail("reporttest@example.com");
        user.setPassword("test123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user = userRepository.save(user);
    }

    @Test
    void shouldReturnDailyOrderSummary() {

        Order order1 = buildOrder(2);
        Order savedOrder1 = orderService.createOrder(order1);

        Order order2 = buildOrder(1);
        Order savedOrder2 = orderService.createOrder(order2);

        LocalDate today = LocalDate.now();

        DailyOrderReport report =
                orderService.getDailyOrderReport(today);

        assertThat(report).isNotNull();

        assertThat(report.getDate())
                .isEqualTo(today);

        assertThat(report.getTotalOrders())
                .isEqualTo(2L);

        assertThat(report.getTotalRevenue())
                .isEqualByComparingTo("3600.00");

        assertThat(savedOrder1.getTotalAmount())
                .isEqualByComparingTo("2400.00");

        assertThat(savedOrder2.getTotalAmount())
                .isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldReturnZeroForDateWithoutOrders() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        DailyOrderReport report =
                orderService.getDailyOrderReport(tomorrow);

        assertThat(report).isNotNull();

        assertThat(report.getDate())
                .isEqualTo(tomorrow);

        assertThat(report.getTotalOrders())
                .isEqualTo(0L);

        assertThat(report.getTotalRevenue())
                .isEqualByComparingTo(BigDecimal.ZERO);
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