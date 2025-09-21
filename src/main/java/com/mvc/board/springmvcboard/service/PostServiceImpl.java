package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostResponseDto::fromSummary)
                .toList();
    }

    @Override
    @Transactional
    public PostResponseDto createPost(PostCreateDto createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException("PostCreateDto cannot be null");
        }

        Post post = new Post(createDto.title(), createDto.content());
        Post savedPost = postRepository.save(post);

        return PostResponseDto.from(savedPost);
    }
}
