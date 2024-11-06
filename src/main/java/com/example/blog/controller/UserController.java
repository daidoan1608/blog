package com.example.blog.controller;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{id}")
    public String viewProfile(@PathVariable UUID id, Model model) {
        UserDto userDto = userService.findById(id);
        List<PostDto> posts = postService.findAllByUserId(id);
        if(userDto.getAvatar() != null) {
            model.addAttribute("path", userDto.getAvatar());
        }
        model.addAttribute("user",userDto);
        model.addAttribute("posts", posts);
        return "profile";
    }
}
