package com.example.blog.repository;

import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsernameAndPassword(String username, String password);

    User findByUsername(String username);

    void deleteById(UUID id);
}
