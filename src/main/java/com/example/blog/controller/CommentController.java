package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.service.Impl.CommentServiceImpl;
import com.example.blog.service.Impl.PostServiceImpl;
import com.example.blog.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable UUID id) {
        log.info("Comment deleted");
        commentService.deleteComment(id);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String updateComment(@PathVariable UUID id,
                                Model model) {
        log.info("Load Comment updated view");
        CommentDto comment = commentService.findById(id);
        model.addAttribute("comment", comment);
        return "update-comment";
    }

    @PostMapping("/update/{id}")
    public String updateComment(@PathVariable UUID id,
                                @RequestParam String body) {
        log.info("Comment updated");
        CommentDto comment = commentService.findById(id);
        comment.setBody(body);
        commentService.updateComment(comment);
        return "redirect:/";
    }

}
