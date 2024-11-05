package com.example.blog.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PostDto {
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserDto author;
}
