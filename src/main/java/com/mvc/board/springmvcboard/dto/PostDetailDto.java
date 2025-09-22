package com.mvc.board.springmvcboard.dto;

import com.mvc.board.springmvcboard.entity.Post;

import java.util.List;

public record PostDetailDto(
        Long id,
        String title,
        String content,
        Integer viewCount,
        List<CommentResponseDto> comments
) {
    public static PostDetailDto from(Post post) {
        List<CommentResponseDto> comments = post.getComments().stream().map(CommentResponseDto::from).toList();

        return new PostDetailDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                comments
        );
    }
}
