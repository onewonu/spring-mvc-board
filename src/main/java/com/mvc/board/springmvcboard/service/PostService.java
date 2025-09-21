package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.*;

import java.util.List;

public interface PostService {
    List<PostResponseDto> getAllPosts();
}
