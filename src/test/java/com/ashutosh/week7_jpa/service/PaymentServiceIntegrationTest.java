package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.OrderItem;
import com.ashutosh.week7_jpa.entity.Payment;
import com.ashutosh.week7_jpa.entity.PaymentStatus;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;

import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.OrderRepository;
import com.ashutosh.week7_jpa.repository.PaymentRepository;
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
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Order savedOrder;

    @BeforeEach
    void setUp() {

        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        Category category = new Category();
        category.setName("Payment Test Category");
        category.setDescription("Category for payment tests");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Payment Test Product");
        product.setDescription("Product for payment tests");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQuantity(10);
        product.setActive(true);
        product.setCategory(category);
        product = productRepository.save(product);

        User user = new User();
        user.setName("Payment Test User");
        user.setEmail("paymenttest@example.com");
        user.setPassword("test123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user = userRepository.save(user);

        User orderUser = new User();
        orderUser.setId(user.getId());

        Product orderProduct = new Product();
        orderProduct.setId(product.getId());

        OrderItem item = new OrderItem();
        item.setProduct(orderProduct);
        item.setQuantity(1);

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        Order order = new Order();
        order.setUser(orderUser);
        order.setItems(items);

        savedOrder = orderService.createOrder(order);
    }

    @Test
    void shouldCreatePaymentForOrder() {

        Payment payment = paymentService.createPayment(
                savedOrder.getId(),
                "UPI",
                "TEST-TXN-001"
        );

        assertThat(payment.getId()).isNotNull();

        assertThat(payment.getAmount())
                .isEqualByComparingTo("1500.00");

        assertThat(payment.getStatus())
                .isEqualTo(PaymentStatus.PENDING);

        assertThat(payment.getPaymentMethod())
                .isEqualTo("UPI");

        assertThat(payment.getTransactionId())
                .isEqualTo("TEST-TXN-001");
    }

    @Test
    void shouldRejectDuplicatePaymentForSameOrder() {

        paymentService.createPayment(
                savedOrder.getId(),
                "UPI",
                "TEST-TXN-002"
        );

        assertThatThrownBy(() ->
                paymentService.createPayment(
                        savedOrder.getId(),
                        "CARD",
                        "TEST-TXN-003"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldUpdatePaymentStatus() {

        Payment payment = paymentService.createPayment(
                savedOrder.getId(),
                "UPI",
                "TEST-TXN-004"
        );

        Payment updatedPayment =
                paymentService.updatePaymentStatus(
                        payment.getId(),
                        PaymentStatus.COMPLETED
                );

        assertThat(updatedPayment.getStatus())
                .isEqualTo(PaymentStatus.COMPLETED);
    }
}