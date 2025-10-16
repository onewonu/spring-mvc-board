package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;
import com.mvc.board.springmvcboard.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
            @Valid @ModelAttribute CommentCreateDto createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_ATTRIBUTE,
                    bindingResult.getAllErrors().get(0).getDefaultMessage()
            );
            return redirectToPost(postId);
        }

        commentService.createComment(postId, createDto);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 작성되었습니다.");
        return redirectToPost(postId);
    }

    @PutMapping("/comments/{commentId}")
    public String updateComment(
            @PathVariable Long commentId,
            @RequestParam Long postId,
            @Valid @ModelAttribute CommentUpdateDto updateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_ATTRIBUTE,
                    bindingResult.getAllErrors().get(0).getDefaultMessage()
            );
            return redirectToPost(postId);
        }

        commentService.updateComment(commentId, updateDto);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 수정되었습니다.");
        return redirectToPost(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long postId,
            RedirectAttributes redirectAttributes
    ) {
        commentService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "댓글이 삭제되었습니다.");
        return redirectToPost(postId);
    }
}