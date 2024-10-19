package com.example.blog.service.Impl;

import com.example.blog.dto.UserDto;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;



import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UserDto findByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user != null ? modelMapper.map(user, UserDto.class) : null;
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        var user = modelMapper.map(userDto, User.class);
        var savedUser = userRepository.save(user);
        return userDto;
    }

    @Override
    public void update(UUID id, UserDto userDto) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.setFullname(userDto.getFullname());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setRole(userDto.getRole());
        existingUser.setAvatar(userDto.getAvatar());

        var updatedUser = userRepository.save(existingUser);
        modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return modelMapper.map(user, UserDto.class);
    }
}
