package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;
import com.mvc.board.springmvcboard.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {

    private final CommentService commentService;

    private static final String REDIRECT_POSTS_BASE = "redirect:/posts";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "error";

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private String redirectToPost(Long postId) {
        return REDIRECT_POSTS_BASE + "/" + postId;
    }

    @PostMapping("/posts/{postId}/comments")
    public String createComment(
            @PathVariable Long postId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes
    ) {
        try {
            CommentCreateDto createDto = CommentCreateDto.of(content);
            commentService.createComment(postId, createDto);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 작성되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
        }

        return redirectToPost(postId);
    }

    @PutMapping("/comments/{commentId}")
    public String updateComment(
            @PathVariable Long commentId,
            @RequestParam Long postId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes
    ) {
        try {
            CommentUpdateDto updateDto = CommentUpdateDto.of(content);
            commentService.updateComment(commentId, updateDto);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
        }

        return redirectToPost(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long postId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            commentService.deleteComment(commentId);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, e.getMessage());
        }

        return redirectToPost(postId);
    }
}