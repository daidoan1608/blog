package com.example.blog.service;

import com.example.blog.dto.PostDto;

import java.util.List;
import java.util.UUID;

public interface PostService {
    void createPost(PostDto postDto, UUID userId);

    void updatePost(UUID id, PostDto postDto);

    void deletePost(UUID id);

    List<PostDto> getAllPosts();

    PostDto getPost(UUID id);
}
