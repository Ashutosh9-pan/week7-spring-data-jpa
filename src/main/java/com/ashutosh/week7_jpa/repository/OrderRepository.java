package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.OrderStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "user",
            "items",
            "items.product",
            "items.product.category",
            "payment"
    })
    @Query("""
        SELECT DISTINCT o FROM Order o
        WHERE o.user.id = :userId
        ORDER BY o.orderDate DESC
        """)
    List<Order> findByUserIdOrderByOrderDateDesc(
            @Param("userId") Long userId
    );

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByOrderDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @EntityGraph(attributePaths = {
            "user",
            "items",
            "items.product",
            "items.product.category",
            "payment"
    })
    @Query("""
        SELECT DISTINCT o FROM Order o
        ORDER BY o.orderDate DESC
        """)
    List<Order> findAllWithDetails();

    @EntityGraph(attributePaths = {
            "user",
            "items",
            "items.product",
            "items.product.category",
            "payment"
    })
    @Query("""
        SELECT DISTINCT o FROM Order o
        WHERE o.id = :id
        """)
    Optional<Order> findByIdWithDetails(
            @Param("id") Long id
    );

    @EntityGraph(attributePaths = {
            "user",
            "items",
            "items.product",
            "items.product.category",
            "payment"
    })
    @Query("""
        SELECT DISTINCT o FROM Order o
        WHERE (:status IS NULL OR o.status = :status)
          AND (:userId IS NULL OR o.user.id = :userId)
        ORDER BY o.orderDate DESC
        """)
    List<Order> searchOrders(
            @Param("status") OrderStatus status,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT COUNT(o), COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderDate >= :startDate
          AND o.orderDate < :endDate
        """)
    List<Object[]> getDailyOrderSummary(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}