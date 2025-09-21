package com.mvc.board.springmvcboard.dto;

public record CommentCreateDto(String content) {
    public static CommentCreateDto of(String content) {
        return new CommentCreateDto(content != null ? content.trim() : null);
    }
}
