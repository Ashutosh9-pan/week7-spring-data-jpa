package com.ashutosh.week7_jpa.audit;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class JpaAuditingIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldAutomaticallySetCreatedAtAndUpdatedAt() throws Exception {

        Category category = new Category();
        category.setName("Audit Test Category");
        category.setDescription("Testing Spring Data JPA auditing");

        Category savedCategory = categoryRepository.saveAndFlush(category);

        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getUpdatedAt()).isNotNull();

        var originalCreatedAt = savedCategory.getCreatedAt();
        var originalUpdatedAt = savedCategory.getUpdatedAt();

        Thread.sleep(20);

        savedCategory.setDescription("Updated auditing test description");

        Category updatedCategory =
                categoryRepository.saveAndFlush(savedCategory);

        assertThat(updatedCategory.getCreatedAt())
                .isEqualTo(originalCreatedAt);

        assertThat(updatedCategory.getUpdatedAt())
                .isAfterOrEqualTo(originalUpdatedAt);
    }
}