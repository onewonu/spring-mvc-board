package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostDetailDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.dto.PostUpdateDto;
import com.mvc.board.springmvcboard.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private static final String REDIRECT_POSTS_URL = "redirect:/posts/";

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String getAllPosts(Model model) {
        List<PostResponseDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/new")
    public String createPostForm(Model model) {
        model.addAttribute("post", new PostCreateDto("", ""));
        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute PostCreateDto createDto, RedirectAttributes redirectAttributes) {
        try {
            PostResponseDto savedPost = postService.createPost(createDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
            return REDIRECT_POSTS_URL + savedPost.id();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("post", createDto);
            return REDIRECT_POSTS_URL + "new";
        }
    }

    @GetMapping("/{id}")
    public String getPostDetail(@PathVariable Long id, Model model) {
        PostDetailDto post = postService.getPostDetail(id);
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        PostDetailDto post = postService.getPostDetail(id);
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PatchMapping("/{id}")
    public String updatePost(
            @PathVariable Long id,
            @ModelAttribute PostUpdateDto updateDto,
            RedirectAttributes redirectAttributes
    ) {
        try {
            postService.updatePost(id, updateDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
            return REDIRECT_POSTS_URL + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return REDIRECT_POSTS_URL + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/posts";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return REDIRECT_POSTS_URL + id;
        }
    }
}
