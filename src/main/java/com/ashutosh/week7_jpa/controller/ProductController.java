package com.ashutosh.week7_jpa.controller;

import com.ashutosh.week7_jpa.dto.ProductRequest;
import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getProducts(

            @RequestParam(required = false) String name,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) BigDecimal minPrice,

            @RequestParam(required = false) BigDecimal maxPrice,

            @RequestParam(required = false) Integer page,

            @RequestParam(required = false) Integer size,

            @RequestParam(required = false) String sortBy,

            @RequestParam(required = false) String direction
    ) {

        boolean paginationRequested =
                page != null || size != null;

        if (paginationRequested) {

            int requestedPage = page != null ? page : 0;
            int requestedSize = size != null ? size : 10;

            Page<Product> products =
                    productService.searchProductsPaginated(
                            name,
                            categoryId,
                            minPrice,
                            maxPrice,
                            requestedPage,
                            requestedSize,
                            sortBy,
                            direction
                    );

            return ResponseEntity.ok(products);
        }

        if (name != null
                || categoryId != null
                || minPrice != null
                || maxPrice != null) {

            return ResponseEntity.ok(
                    productService.searchProducts(
                            name,
                            categoryId,
                            minPrice,
                            maxPrice
                    )
            );
        }

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        Category category = new Category();
        category.setId(request.getCategoryId());

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(request.getActive());
        product.setCategory(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        Category category = new Category();
        category.setId(request.getCategoryId());

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(request.getActive());
        product.setCategory(category);

        return ResponseEntity.ok(
                productService.updateProduct(id, product)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}