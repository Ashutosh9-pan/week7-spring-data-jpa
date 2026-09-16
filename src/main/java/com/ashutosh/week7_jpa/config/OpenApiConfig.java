package com.ashutosh.week7_jpa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI week7OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Week 7 - E-Commerce Backend API")
                        .version("1.0.0")
                        .description(
                                "Spring Boot e-commerce backend with " +
                                "Spring Data JPA, PostgreSQL, Flyway, " +
                                "transactions, caching, auditing, " +
                                "pagination and payment processing."
                        )
                );
    }
}
