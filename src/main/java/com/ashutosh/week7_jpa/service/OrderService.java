package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.dto.DailyOrderReport;
import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.OrderItem;
import com.ashutosh.week7_jpa.entity.OrderStatus;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.exception.ResourceNotFoundException;
import com.ashutosh.week7_jpa.repository.OrderRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;
import com.ashutosh.week7_jpa.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository
                .findByUserIdOrderByOrderDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> searchOrders(
            OrderStatus status,
            Long userId
    ) {
        return orderRepository.searchOrders(
                status,
                userId
        );
    }

    @Transactional(readOnly = true)
    public DailyOrderReport getDailyOrderReport(LocalDate date) {

        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.plusDays(1).atStartOfDay();

        List<Object[]> results =
                orderRepository.getDailyOrderSummary(
                        startDate,
                        endDate
                );

        Object[] result;

        if (results == null || results.isEmpty()) {
            result = new Object[]{
                    0L,
                    BigDecimal.ZERO
            };
        } else {
            result = results.get(0);
        }

        Long totalOrders =
                result[0] instanceof Number
                        ? ((Number) result[0]).longValue()
                        : 0L;

        BigDecimal totalRevenue;

        if (result[1] == null) {
            totalRevenue = BigDecimal.ZERO;
        } else if (result[1] instanceof BigDecimal) {
            totalRevenue = (BigDecimal) result[1];
        } else if (result[1] instanceof Number) {
            totalRevenue = BigDecimal.valueOf(
                    ((Number) result[1]).doubleValue()
            );
        } else {
            totalRevenue = BigDecimal.ZERO;
        }

        return new DailyOrderReport(
                date,
                totalOrders,
                totalRevenue
        );
    }

    public Order createOrder(Order order) {

        if (order.getUser() == null
                || order.getUser().getId() == null) {

            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        Long userId = order.getUser().getId();

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        if (order.getItems() == null
                || order.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {

            if (item.getProduct() == null
                    || item.getProduct().getId() == null) {

                throw new IllegalArgumentException(
                        "Product id is required for every order item"
                );
            }

            if (item.getQuantity() == null
                    || item.getQuantity() < 1) {

                throw new IllegalArgumentException(
                        "Quantity must be at least 1"
                );
            }

            Long productId =
                    item.getProduct().getId();

            Product product = productRepository
                    .findByIdForUpdate(productId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: "
                                            + productId
                            )
                    );

            if (!product.getActive()) {
                throw new IllegalArgumentException(
                        "Product is inactive: "
                                + product.getName()
                );
            }

            if (product.getStockQuantity()
                    < item.getQuantity()) {

                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            product.setStockQuantity(
                    product.getStockQuantity()
                            - item.getQuantity()
            );

            productRepository.save(product);

            item.setOrder(order);
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.calculateSubtotal();

            totalAmount = totalAmount.add(
                    item.getSubtotal()
            );
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(
            Long orderId,
            OrderStatus status
    ) {

        Order order = getOrderById(orderId);

        order.setStatus(status);

        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {

        Order order = getOrderById(orderId);

        if (order.getStatus()
                == OrderStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Order is already cancelled"
            );
        }

        if (order.getStatus()
                == OrderStatus.DELIVERED) {

            throw new IllegalArgumentException(
                    "Delivered order cannot be cancelled"
            );
        }

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity()
                            + item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }
}