package com.mvc.board.springmvcboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateDto(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다")
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
