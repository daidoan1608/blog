package com.example.blog.repository;

import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);

    void deleteById(UUID id);

    List<User> findByUsernameContainingIgnoreCase(String username);

}
