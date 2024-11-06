package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {
    private String body;
    private UserDto user;
    private PostDto post;
    private LocalDateTime updatedAt;
}