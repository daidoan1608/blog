package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.service.Impl.CommentServiceImpl;
import com.example.blog.service.Impl.PostServiceImpl;
import com.example.blog.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;
    private final CommentServiceImpl commentService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            UserDto user = userServiceImpl.findByUsername(username);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("message", "Người dùng không tồn tại.");
                log.error("Người dùng không tồn tại.");
            }
        }
        List<PostDto> posts = postServiceImpl.getAllPosts();
        for (PostDto post : posts) {
            List<CommentDto> comments = commentService.getCommentsByPostId(post.getId());
            post.setComments(comments);
        }
        model.addAttribute("posts", posts);
        return "home";
    }
}
