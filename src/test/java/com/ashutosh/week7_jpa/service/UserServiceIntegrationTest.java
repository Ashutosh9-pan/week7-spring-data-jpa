package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;
import com.ashutosh.week7_jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWithHashedPassword() {

        User user = new User();
        user.setName("Login Test User");
        user.setEmail("login@example.com");
        user.setPassword("secret123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);

        User savedUser = userService.registerUser(user);

        assertThat(savedUser.getId())
                .isNotNull();

        assertThat(savedUser.getPassword())
                .isNotEqualTo("secret123");

        assertThat(savedUser.getPassword())
                .startsWith("$2");

        assertThat(
                userService.getUserByEmail("login@example.com")
                        .getPassword()
        )
                .isNotEqualTo("secret123");
    }

    @Test
    void shouldLoginWithCorrectPassword() {

        User user = new User();
        user.setName("Successful Login User");
        user.setEmail("success@example.com");
        user.setPassword("secret123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);

        userService.registerUser(user);

        User loggedInUser =
                userService.login(
                        "success@example.com",
                        "secret123"
                );

        assertThat(loggedInUser)
                .isNotNull();

        assertThat(loggedInUser.getEmail())
                .isEqualTo("success@example.com");

        assertThat(loggedInUser.getRole())
                .isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void shouldRejectLoginWithWrongPassword() {

        User user = new User();
        user.setName("Wrong Password User");
        user.setEmail("wrong@example.com");
        user.setPassword("secret123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);

        userService.registerUser(user);

        assertThatThrownBy(() ->
                userService.login(
                        "wrong@example.com",
                        "wrongpassword"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldRejectLoginForInactiveUser() {

        User user = new User();
        user.setName("Inactive User");
        user.setEmail("inactive@example.com");
        user.setPassword("secret123");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(false);

        user = userRepository.save(user);

        assertThatThrownBy(() ->
                userService.login(
                        "inactive@example.com",
                        "secret123"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User account is inactive");
    }

    @Test
    void shouldRejectLoginForUnknownEmail() {

        assertThatThrownBy(() ->
                userService.login(
                        "unknown@example.com",
                        "secret123"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }
}