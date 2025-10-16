package com.mvc.board.springmvcboard.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
    public static CommentCreateDto of(String content) {
        return new CommentCreateDto(content != null ? content.trim() : null);
    }
}
