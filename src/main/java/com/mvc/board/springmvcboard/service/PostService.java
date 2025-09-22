package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.*;

import java.util.List;

public interface PostService {
    List<PostResponseDto> getAllPosts();

    PostResponseDto createPost(PostCreateDto createDto);

    PostDetailDto getPostDetail(Long postId);
}
