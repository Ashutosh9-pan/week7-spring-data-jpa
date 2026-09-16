package com.ashutosh.week7_jpa.config;

import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;
import com.ashutosh.week7_jpa.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class DataSeederTest {

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldNotCreateDuplicateSeedData() throws Exception {

        long initialCategoryCount =
                categoryRepository.count();

        long initialProductCount =
                productRepository.count();

        long initialUserCount =
                userRepository.count();

        dataSeeder.run();
        dataSeeder.run();

        long finalCategoryCount =
                categoryRepository.count();

        long finalProductCount =
                productRepository.count();

        long finalUserCount =
                userRepository.count();

        assertThat(finalCategoryCount)
                .isEqualTo(initialCategoryCount);

        assertThat(finalProductCount)
                .isEqualTo(initialProductCount);

        assertThat(finalUserCount)
                .isEqualTo(initialUserCount);

        assertThat(
                categoryRepository.existsByNameIgnoreCase(
                        "Electronics"
                )
        ).isTrue();

        assertThat(
                userRepository.existsByEmailIgnoreCase(
                        "demo@example.com"
                )
        ).isTrue();

        User demoUser = userRepository
                .findByEmailIgnoreCase("demo@example.com")
                .orElseThrow();

        assertThat(demoUser.getName())
                .isEqualTo("Demo User");

        assertThat(demoUser.getEmail())
                .isEqualTo("demo@example.com");

        assertThat(demoUser.isActive())
                .isTrue();

        assertThat(
                productRepository
                        .findByNameContainingIgnoreCase(
                                "Wireless Headphones"
                        )
                        .size()
        ).isEqualTo(1);
    }
}