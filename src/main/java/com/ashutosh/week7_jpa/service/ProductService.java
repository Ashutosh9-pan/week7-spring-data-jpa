package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.exception.ResourceNotFoundException;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {

        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        return productRepository.searchProducts(
                name,
                categoryId,
                minPrice,
                maxPrice
        );
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProductsPaginated(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        String safeSortBy = "name";

        if (sortBy != null && !sortBy.isBlank()) {

            safeSortBy = switch (sortBy) {

                case "id", "name", "price",
                     "stockQuantity", "createdAt" ->
                        sortBy;

                default ->
                        throw new IllegalArgumentException(
                                "Invalid sort field: " + sortBy
                        );
            };
        }

        Sort.Direction sortDirection =
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, safeSortBy)
        );

        return productRepository.searchProductsPaginated(
                name,
                categoryId,
                minPrice,
                maxPrice,
                pageable
        );
    }

    @CachePut(value = "products", key = "#result.id")
    public Product createProduct(Product product) {

        if (product.getCategory() == null
                || product.getCategory().getId() == null) {

            throw new IllegalArgumentException(
                    "Category id is required"
            );
        }

        Category category = categoryRepository
                .findById(product.getCategory().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with id: "
                                        + product.getCategory().getId()
                        )
                );

        product.setCategory(category);

        return productRepository.save(product);
    }

    @CachePut(value = "products", key = "#id")
    public Product updateProduct(
            Long id,
            Product updatedProduct
    ) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(
                updatedProduct.getName()
        );

        existingProduct.setDescription(
                updatedProduct.getDescription()
        );

        existingProduct.setPrice(
                updatedProduct.getPrice()
        );

        existingProduct.setStockQuantity(
                updatedProduct.getStockQuantity()
        );

        existingProduct.setActive(
                updatedProduct.getActive()
        );

        if (updatedProduct.getCategory() != null
                && updatedProduct.getCategory().getId() != null) {

            Category category = categoryRepository
                    .findById(
                            updatedProduct.getCategory().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found with id: "
                                            + updatedProduct
                                            .getCategory()
                                            .getId()
                            )
                    );

            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }
}