package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.PostDto;
import com.example.blog.model.CustomUserDetails;
import com.example.blog.service.Impl.CommentServiceImpl;
import com.example.blog.service.Impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes("post")
@RequestMapping("/posts")
public class PostController {
    private final PostServiceImpl postService;
    private final CommentServiceImpl commentService;
    @PostMapping("/create")
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam String title,
                             @RequestParam String content) {
        UUID authorId = userDetails.getId();
        PostDto postDto = new PostDto();
        postDto.setTitle(title);
        postDto.setContent(content);
        postService.createPost(postDto, authorId);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable UUID id,
                          Model model) {
        PostDto post = postService.findById(id);
        List<CommentDto> comments = commentService.getComments(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("user", null);
        } else {
            model.addAttribute("user", authentication.getPrincipal());
        }
        return "post";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable UUID id) {
        PostDto post = postService.findById(id);

        if (post == null) {
            log.warn("Bài viết không tồn tại");
            return "redirect:/";
        }
        if (post.getAuthor().getId().equals(userDetails.getId())) {
            log.warn("Bạn không có quyền xóa bài viết này");
        }

        postService.deletePost(id);
        log.info("Xóa bài viết thành công");
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String updatePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable UUID id,
                             @RequestParam String title,
                             @RequestParam String content) {
        PostDto post = postService.findById(id);
        if (post==null) {
            log.warn("Bài viết không tồn tại");
            return "redirect:/";
        }
        if (post.getAuthor().getId().equals(userDetails.getId())) {
            log.warn("Bạn không có sửa bài viết này");
        }
        post.setTitle(title);
        post.setContent(content);
        postService.updatePost(id, post);
        log.info("sửa bài viết thành công");
        return "redirect:/";
    }
}
