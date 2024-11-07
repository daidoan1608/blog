package com.example.blog.service;

import com.example.blog.dto.CommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    void createComment(UUID postId, CommentDto commentDto);
    List<CommentDto> getComments(UUID postId);
    void deleteComment(UUID commentId);
    CommentDto findById(UUID id);
    void updateComment(CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(UUID postId);
}
