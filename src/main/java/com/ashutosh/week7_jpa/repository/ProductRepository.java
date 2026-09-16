package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.Product;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByPriceBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(
            @Param("id") Long id
    );

    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p ORDER BY p.name ASC")
    List<Product> findAllWithCategory();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(
            @Param("id") Long id
    );

    @EntityGraph(attributePaths = "category")
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.active = true
              AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            ORDER BY p.name ASC
            """)
    List<Product> searchProducts(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @EntityGraph(attributePaths = "category")
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.active = true
              AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> searchProductsPaginated(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}