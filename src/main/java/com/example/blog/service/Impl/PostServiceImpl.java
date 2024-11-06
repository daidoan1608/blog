package com.example.blog.service.Impl;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper postMapper;

    @Override
    public void createPost(PostDto postDto, UUID userId) {
        User author = userRepository.findById(userId).orElseThrow();
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setUser(author);
        postRepository.save(post);
    }

    @Override
    public void updatePost(UUID id, PostDto postDto) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postRepository.save(post);
    }

    @Override
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> {
                    PostDto postDto = postMapper.map(post, PostDto.class);
                    if (post.getUser() != null) {
                        UserDto userDto = postMapper.map(post.getUser(), UserDto.class);
                        postDto.setAuthor(userDto);
                    }
                    return postDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PostDto findById(UUID id) {
        Post post = postRepository.findById(id).orElseThrow();

        // Ánh xạ post sang PostDto
        PostDto postDto = postMapper.map(post, PostDto.class);

        // Kiểm tra xem post có user không, nếu có thì ánh xạ user sang UserDto
        if (post.getUser() != null) {
            UserDto userDto = postMapper.map(post.getUser(), UserDto.class);
            postDto.setAuthor(userDto); // Đặt author cho postDto
        }

        return postDto;
    }

    @Override
    public List<PostDto> findAllByUserId(UUID userId) {
        List<Post> posts = postRepository.findAllByUserId(userId);
        return posts.stream()
                .map(post -> {
                    PostDto postDto = postMapper.map(post, PostDto.class);
                    if (post.getUser() != null) {
                        UserDto userDto = postMapper.map(post.getUser(), UserDto.class);
                        postDto.setAuthor(userDto);
                    }
                    return postDto;
                })
                .collect(Collectors.toList());
    }
}
