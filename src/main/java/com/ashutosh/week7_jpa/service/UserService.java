package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;
import com.ashutosh.week7_jpa.exception.ResourceNotFoundException;
import com.ashutosh.week7_jpa.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }

    public User registerUser(User user) {

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + user.getEmail()
            );
        }

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setActive(true);

        return userRepository.save(user);
    }

    public User login(
            String email,
            String password
    ) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        if (password == null
                || !passwordEncoder.matches(
                        password,
                        user.getPassword()
                )) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    public User updateUser(
            Long id,
            User updatedUser
    ) {

        User existingUser = getUserById(id);

        if (updatedUser.getEmail() != null
                && !updatedUser.getEmail()
                .equalsIgnoreCase(existingUser.getEmail())
                && userRepository.existsByEmailIgnoreCase(
                        updatedUser.getEmail()
                )) {

            throw new IllegalArgumentException(
                    "Email already registered: "
                            + updatedUser.getEmail()
            );
        }

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        existingUser.setActive(
                updatedUser.isActive()
        );

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }
}