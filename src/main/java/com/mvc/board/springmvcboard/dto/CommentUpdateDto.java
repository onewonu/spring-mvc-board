package com.mvc.board.springmvcboard.dto;

public record CommentUpdateDto(String content) {
    public static CommentUpdateDto of(String content) {
        return new CommentUpdateDto(content != null ? content.trim() : null);
    }
}
