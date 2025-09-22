package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;
import com.mvc.board.springmvcboard.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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
            redirectAttributes.addFlashAttribute("message", "댓글이 작성되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/posts/" + postId;
    }

    @PutMapping("/comments/{commentId}/edit")
    public String updateComment(
            @PathVariable Long commentId,
            @RequestParam Long postId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes
    ) {
        try {
            CommentUpdateDto updateDto = CommentUpdateDto.of(content);
            commentService.updateComment(commentId, updateDto);
            redirectAttributes.addFlashAttribute("message", "댓글이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/posts/" + postId;
    }

}
