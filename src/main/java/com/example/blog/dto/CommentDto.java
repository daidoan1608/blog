package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentDto {
    private UUID id;
    private String body;
    private UserDto user;
    private PostDto post;
    private LocalDateTime updatedAt;
}