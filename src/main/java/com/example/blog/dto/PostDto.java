package com.example.blog.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PostDto {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private UserDto author;
    private List<CommentDto> comments;
}
