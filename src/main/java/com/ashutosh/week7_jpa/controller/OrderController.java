package com.ashutosh.week7_jpa.controller;

import com.ashutosh.week7_jpa.dto.DailyOrderReport;
import com.ashutosh.week7_jpa.dto.OrderItemRequest;
import com.ashutosh.week7_jpa.dto.OrderRequest;
import com.ashutosh.week7_jpa.dto.OrderStatusRequest;
import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.OrderItem;
import com.ashutosh.week7_jpa.entity.OrderStatus;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {

        User user = new User();
        user.setId(request.getUserId());

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = new Product();
            product.setId(itemRequest.getProductId());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());

            items.add(item);
        }

        order.setItems(items);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long userId
    ) {

        if (status != null || userId != null) {
            return ResponseEntity.ok(
                    orderService.searchOrders(status, userId)
            );
        }

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @GetMapping("/orders/report/daily")
    public ResponseEntity<DailyOrderReport> getDailyOrderReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        LocalDate reportDate =
                date != null ? date : LocalDate.now();

        return ResponseEntity.ok(
                orderService.getDailyOrderReport(reportDate)
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request
    ) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        request.getStatus()
                )
        );
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id
    ) {

        orderService.cancelOrder(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );
    }
}