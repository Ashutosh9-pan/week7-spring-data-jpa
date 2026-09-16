package com.ashutosh.week7_jpa.controller;

import com.ashutosh.week7_jpa.dto.CategoryRequest;
import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    @GetMapping("/root")
    public ResponseEntity<List<Category>> getRootCategories() {
        return ResponseEntity.ok(
                categoryService.getRootCategories()
        );
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> getChildCategories(
            @PathVariable Long parentId
    ) {
        return ResponseEntity.ok(
                categoryService.getChildCategories(parentId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        Category category = new Category(
                request.getName(),
                request.getDescription()
        );

        if (request.getParentId() != null) {
            Category parent = new Category();
            parent.setId(request.getParentId());
            category.setParent(parent);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {

        Category category = new Category(
                request.getName(),
                request.getDescription()
        );

        if (request.getParentId() != null) {
            Category parent = new Category();
            parent.setId(request.getParentId());
            category.setParent(parent);
        }

        return ResponseEntity.ok(
                categoryService.updateCategory(id, category)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}