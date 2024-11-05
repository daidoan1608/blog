package com.example.blog.controller;

import com.example.blog.dto.UserDto;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String viewProfile(@PathVariable UUID id, Model model) {
        // Load và trả về thông tin hồ sơ
        UserDto userDto = userService.findById(id);
        if(userDto.getAvatar() != null) {
            model.addAttribute("path", userDto.getAvatar());
        }
        model.addAttribute("user",userDto);
        return "profile";
    }
}
