package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;

public interface CommentService {
    CommentResponseDto createComment(Long postId, CommentCreateDto createDto);

    CommentResponseDto updateComment(Long commentId, CommentUpdateDto updateDto);
}
