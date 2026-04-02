package com.feedback.repository;

import com.feedback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndRole(String username, User.Role role);
    Optional<User> findByEmailAndRole(String email, User.Role role);
    List<User> findByRole(User.Role role);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
