package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;
    private Category accessories;

    @BeforeEach
    void setUp() {

        productRepository.deleteAll();
        categoryRepository.deleteAll();

        electronics = new Category();
        electronics.setName("Repository Electronics");
        electronics.setDescription("Electronics category");
        electronics = categoryRepository.save(electronics);

        accessories = new Category();
        accessories.setName("Repository Accessories");
        accessories.setDescription("Accessories category");
        accessories = categoryRepository.save(accessories);

        Product laptop = new Product();
        laptop.setName("Gaming Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(new BigDecimal("70000.00"));
        laptop.setStockQuantity(5);
        laptop.setActive(true);
        laptop.setCategory(electronics);

        Product headphones = new Product();
        headphones.setName("Wireless Headphones");
        headphones.setDescription("Bluetooth headphones");
        headphones.setPrice(new BigDecimal("3000.00"));
        headphones.setStockQuantity(20);
        headphones.setActive(true);
        headphones.setCategory(accessories);

        Product inactiveProduct = new Product();
        inactiveProduct.setName("Old Keyboard");
        inactiveProduct.setDescription("Inactive product");
        inactiveProduct.setPrice(new BigDecimal("1500.00"));
        inactiveProduct.setStockQuantity(2);
        inactiveProduct.setActive(false);
        inactiveProduct.setCategory(accessories);

        productRepository.saveAll(
                List.of(laptop, headphones, inactiveProduct)
        );

        productRepository.flush();
    }

    @Test
    void shouldFindActiveProducts() {

        List<Product> products =
                productRepository.findByActiveTrue();

        assertThat(products)
                .hasSize(2)
                .allMatch(product -> Boolean.TRUE.equals(product.getActive()));
    }

    @Test
    void shouldSearchProductByNameIgnoringCase() {

        List<Product> products =
                productRepository.findByNameContainingIgnoreCase(
                        "headphones"
                );

        assertThat(products).hasSize(1);

        assertThat(products.get(0).getName())
                .isEqualTo("Wireless Headphones");
    }

    @Test
    void shouldFindProductsByCategory() {

        List<Product> products =
                productRepository.findByCategoryId(
                        accessories.getId()
                );

        assertThat(products).hasSize(2);
    }

    @Test
    void shouldFindProductsWithinPriceRange() {

        List<Product> products =
                productRepository.findByPriceBetween(
                        new BigDecimal("2000.00"),
                        new BigDecimal("5000.00")
                );

        assertThat(products).hasSize(1);

        assertThat(products.get(0).getName())
                .isEqualTo("Wireless Headphones");
    }

    @Test
    void shouldSearchProductsUsingCustomQuery() {

        List<Product> products =
                productRepository.searchProducts(
                        "wireless",
                        accessories.getId(),
                        new BigDecimal("2000.00"),
                        new BigDecimal("4000.00")
                );

        assertThat(products).hasSize(1);

        Product product = products.get(0);

        assertThat(product.getName())
                .isEqualTo("Wireless Headphones");

        assertThat(product.getCategory())
                .isNotNull();

        assertThat(product.getCategory().getName())
                .isEqualTo("Repository Accessories");
    }

    @Test
    void shouldReturnOnlyActiveProductsFromCustomSearch() {

        List<Product> products =
                productRepository.searchProducts(
                        null,
                        accessories.getId(),
                        null,
                        null
                );

        assertThat(products).hasSize(1);

        assertThat(products.get(0).getName())
                .isEqualTo("Wireless Headphones");

        assertThat(products.get(0).getActive())
                .isTrue();
    }

    @Test
    void shouldSearchProductsWithPagination() {

        Page<Product> products =
                productRepository.searchProductsPaginated(
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                1,
                                Sort.by("name").ascending()
                        )
                );

        assertThat(products.getSize())
                .isEqualTo(1);

        assertThat(products.getNumber())
                .isEqualTo(0);

        assertThat(products.getTotalElements())
                .isEqualTo(2);

        assertThat(products.getTotalPages())
                .isEqualTo(2);

        assertThat(products.getContent())
                .hasSize(1);

        assertThat(products.getContent().get(0).getActive())
                .isTrue();
    }
}
