package com.mvc.board.springmvcboard.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateDto(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
    public static CommentUpdateDto of(String content) {
        return new CommentUpdateDto(content != null ? content.trim() : null);
    }
}
