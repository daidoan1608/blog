package com.example.blog.service;

import com.example.blog.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User findByUsernameAndPassword(String username,String password);
    List<User> getAll();
    User create(User user);
    User update(UUID id, User user);
    void deleteById(UUID id);

}
