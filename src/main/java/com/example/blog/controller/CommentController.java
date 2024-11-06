package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.service.Impl.CommentServiceImpl;
import com.example.blog.service.Impl.PostServiceImpl;
import com.example.blog.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes("comment")
@RequestMapping("/comments")
public class CommentController {
    private final CommentServiceImpl commentService;
    private final PostServiceImpl postService;
    private final UserServiceImpl userService;
    @PostMapping("/add")
    public String addComment(@RequestParam String body,
                             @RequestParam UUID postId) {
        log.info("Comment added");
        CommentDto comment = new CommentDto();
        comment.setBody(body);
        comment.setPost(postService.findById(postId));
        comment.setUser(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        commentService.createComment(postId, comment);
        return "redirect:/";
    }
}
