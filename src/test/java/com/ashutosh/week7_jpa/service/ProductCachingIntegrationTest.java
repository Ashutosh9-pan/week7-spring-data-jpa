package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ProductCachingIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private Product product;

    @BeforeEach
    void setUp() {

        Cache cache = cacheManager.getCache("products");

        if (cache != null) {
            cache.clear();
        }

        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Caching Category");
        category.setDescription("Category for caching tests");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Caching Test Product");
        product.setDescription("Product for caching tests");
        product.setPrice(new BigDecimal("2500.00"));
        product.setStockQuantity(10);
        product.setActive(true);
        product.setCategory(category);

        product = productRepository.save(product);

        productRepository.flush();
    }

    @Test
    void shouldCacheProductAfterFirstRead() {

        Cache cache = cacheManager.getCache("products");

        assertThat(cache).isNotNull();

        assertThat(cache.get(product.getId()))
                .isNull();

        Product firstResult =
                productService.getProductById(product.getId());

        assertThat(firstResult)
                .isNotNull();

        assertThat(firstResult.getId())
                .isEqualTo(product.getId());

        assertThat(cache.get(product.getId()))
                .isNotNull();

        Product secondResult =
                productService.getProductById(product.getId());

        assertThat(secondResult)
                .isNotNull();

        assertThat(secondResult.getId())
                .isEqualTo(product.getId());

        assertThat(cache.get(product.getId()))
                .isNotNull();
    }
}