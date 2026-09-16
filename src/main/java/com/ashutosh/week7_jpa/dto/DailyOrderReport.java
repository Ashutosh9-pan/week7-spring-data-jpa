package com.ashutosh.week7_jpa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyOrderReport {

    private LocalDate date;
    private Long totalOrders;
    private BigDecimal totalRevenue;

    public DailyOrderReport() {
    }

    public DailyOrderReport(
            LocalDate date,
            Long totalOrders,
            BigDecimal totalRevenue
    ) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}