package com.example.blog.service;

import com.example.blog.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto findByUsernameAndPassword(String username,String password);
    List<UserDto> getAll();
    UserDto create(UserDto userDto);
    void update(UUID id, UserDto user);
    void deleteById(UUID id);
    UserDto findById(UUID id);

}
