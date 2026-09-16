package com.ashutosh.week7_jpa.dto;

import com.ashutosh.week7_jpa.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatusRequest() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}