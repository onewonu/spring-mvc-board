package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto createComment(Long postId, CommentCreateDto createDto);
}
