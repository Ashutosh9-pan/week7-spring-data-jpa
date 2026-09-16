package com.ashutosh.week7_jpa.repository;

import com.ashutosh.week7_jpa.entity.User;
import com.ashutosh.week7_jpa.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByActiveTrue();

    List<User> findByRole(UserRole role);

    List<User> findByNameContainingIgnoreCase(String name);
}