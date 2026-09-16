package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Category> findByParentIsNull();

    @EntityGraph(attributePaths = "parent")
    List<Category> findByParentId(Long parentId);

    boolean existsByParentId(Long parentId);
}