package com.example.blog.service.Impl;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper commentMapper;


    @Override
    public void createComment(UUID postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        User user = userRepository.findById(commentDto.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Comment comment = commentMapper.map(commentDto, Comment.class);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getComments(UUID postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> {
                    CommentDto commentDto = commentMapper.map(comment, CommentDto.class);
                    commentDto.setUser(commentMapper.map(comment.getUser(), UserDto.class));
                    return commentDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto findById(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentMapper.map(comment, CommentDto.class);
    }

    @Override
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        comment.setBody(commentDto.getBody());
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(UUID postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> {
                    CommentDto commentDto = commentMapper.map(comment, CommentDto.class);
                    commentDto.setUser(commentMapper.map(comment.getUser(), UserDto.class));
                    return commentDto;
                })
                .collect(Collectors.toList());
    }
}
