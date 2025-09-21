package com.mvc.board.springmvcboard.dto;

import com.mvc.board.springmvcboard.entity.Comment;

public record CommentResponseDto(
        Long id,
        String content,
        Long postId
) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId()
        );
    }
}
