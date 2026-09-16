package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
class ProductRepositoryDataJpaTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveAndFindProduct() {

        Category category = new Category();
        category.setName("Data JPA Test Category");
        category.setDescription("Category for DataJpaTest");

        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Data JPA Test Product");
        product.setDescription("Product for DataJpaTest");
        product.setPrice(new BigDecimal("1999.00"));
        product.setStockQuantity(15);
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct =
                productRepository.saveAndFlush(product);

        assertThat(savedProduct.getId())
                .isNotNull();

        Product foundProduct =
                productRepository.findByIdWithCategory(
                        savedProduct.getId()
                ).orElseThrow();

        assertThat(foundProduct.getName())
                .isEqualTo("Data JPA Test Product");

        assertThat(foundProduct.getCategory())
                .isNotNull();

        assertThat(foundProduct.getCategory().getName())
                .isEqualTo("Data JPA Test Category");
    }

    @Test
    void shouldSearchProductsWithPagination() {

        Category category = new Category();
        category.setName("Pagination Test Category");
        category.setDescription("Category for pagination test");

        category = categoryRepository.save(category);

        for (int i = 1; i <= 3; i++) {

            Product product = new Product();
            product.setName("Pagination Product " + i);
            product.setDescription("Pagination test product");
            product.setPrice(
                    new BigDecimal("1000.00")
                            .add(BigDecimal.valueOf(i * 100))
            );
            product.setStockQuantity(10);
            product.setActive(true);
            product.setCategory(category);

            productRepository.save(product);
        }

        productRepository.flush();

        Page<Product> result =
                productRepository.searchProductsPaginated(
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                2,
                                Sort.by("name").ascending()
                        )
                );

        assertThat(result.getTotalElements())
                .isEqualTo(3);

        assertThat(result.getTotalPages())
                .isEqualTo(2);

        assertThat(result.getSize())
                .isEqualTo(2);

        assertThat(result.getContent())
                .hasSize(2);
    }
}