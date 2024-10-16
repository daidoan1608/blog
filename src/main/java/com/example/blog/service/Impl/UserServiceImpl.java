package com.example.blog.service.Impl;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user != null ? modelMapper.map(user, User.class) : null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, User.class)).toList();
    }

    @Override
    public User create(User user) {
        var u = modelMapper.map(user, User.class);
        var savedUser = userRepository.save(u);
        return modelMapper.map(savedUser, User.class);
    }

    @Override
    public User update(UUID id, User user) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.setFullname(user.getFullname());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setAvatar(user.getAvatar()); // Chỉ cần cập nhật avatar như là một String

        var updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, User.class);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
}
