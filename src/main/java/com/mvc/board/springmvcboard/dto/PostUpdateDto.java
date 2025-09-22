package com.mvc.board.springmvcboard.dto;

public record PostUpdateDto(
        String title,
        String content
) {
    public static PostUpdateDto of(String title, String content) {
        return new PostUpdateDto(
                title != null ? title.trim() : null
                , content != null ? content.trim() : null
        );
    }
}
