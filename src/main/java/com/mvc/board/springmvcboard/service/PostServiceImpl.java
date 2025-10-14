package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostDetailDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.dto.PostUpdateDto;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.exception.EntityNotFoundException;
import com.mvc.board.springmvcboard.exception.InvalidInputException;
import com.mvc.board.springmvcboard.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAllWithComments();
        return posts.stream()
                .map(PostResponseDto::fromSummary)
                .toList();
    }

    @Override
    @Transactional
    public PostResponseDto createPost(PostCreateDto createDto) {
        if (createDto == null) {
            throw new InvalidInputException("PostCreateDto cannot be null");
        }

        Post post = new Post(createDto.title(), createDto.content());
        Post savedPost = postRepository.save(post);

        return PostResponseDto.from(savedPost);
    }

    @Override
    @Transactional
    public PostDetailDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> EntityNotFoundException.of("Post", postId));

        post.incrementViewCount();
        return PostDetailDto.from(post);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateDto updateDto) {
        if (updateDto == null) {
            throw new InvalidInputException("PostUpdateDto cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> EntityNotFoundException.of("Post", postId));

        post.updateTitle(updateDto.title());
        post.updateContent(updateDto.content());

        return PostResponseDto.from(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw EntityNotFoundException.of("Post", postId);
        }

        postRepository.deleteById(postId);
    }
}