package com.mvc.board.springmvcboard.dto;

public record PostCreateDto(
        String title,
        String content
) {
    public static PostCreateDto of(String title, String content) {
        return new PostCreateDto(
                title != null ? title.trim() : null
                , content != null ? content.trim() : null
        );
    }
}
