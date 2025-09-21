package com.mvc.board.springmvcboard.dto;

import com.mvc.board.springmvcboard.entity.Post;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        Integer viewCount,
        Integer commentCount
) {
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getComments().size()
        );
    }

    public static PostResponseDto fromSummary(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                null,
                post.getViewCount(),
                post.getComments().size()
        );
    }
}
