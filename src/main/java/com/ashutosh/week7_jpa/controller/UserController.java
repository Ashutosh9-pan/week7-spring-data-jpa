package com.ashutosh.week7_jpa.controller;

import com.ashutosh.week7_jpa.dto.LoginRequest;
import com.ashutosh.week7_jpa.dto.LoginResponse;
import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> registerUser(
            @Valid @RequestBody User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(user));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    @GetMapping("/users/profile")
    public ResponseEntity<User> getUserProfile(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                userService.getUserByEmail(email)
        );
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return ResponseEntity.ok(
                userService.updateUser(id, user)
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}