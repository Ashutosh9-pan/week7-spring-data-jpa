package com.ashutosh.week7_jpa.config;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.entity.Product;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import com.ashutosh.week7_jpa.repository.ProductRepository;
import com.ashutosh.week7_jpa.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Category electronics = categoryRepository
                .findByNameIgnoreCase("Electronics")
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName("Electronics");
                    category.setDescription(
                            "Electronic devices and accessories"
                    );
                    return categoryRepository.save(category);
                });

        boolean productExists = productRepository
                .findByNameContainingIgnoreCase("Wireless Headphones")
                .stream()
                .anyMatch(product ->
                        product.getName()
                                .equalsIgnoreCase("Wireless Headphones")
                );

        if (!productExists) {
            Product product = new Product();
            product.setName("Wireless Headphones");
            product.setDescription("Bluetooth over-ear headphones");
            product.setPrice(new BigDecimal("2499.00"));
            product.setStockQuantity(20);
            product.setActive(true);
            product.setCategory(electronics);

            productRepository.save(product);
        }

        if (!userRepository.existsByEmailIgnoreCase(
                "demo@example.com"
        )) {
            User user = new User();
            user.setName("Demo User");
            user.setEmail("demo@example.com");
            user.setPassword(
                    passwordEncoder.encode("demo123")
            );
            user.setRole(UserRole.CUSTOMER);
            user.setActive(true);

            userRepository.save(user);
        }
    }
}