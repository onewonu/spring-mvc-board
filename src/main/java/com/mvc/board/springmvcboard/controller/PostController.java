package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostDetailDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.dto.PostUpdateDto;
import com.mvc.board.springmvcboard.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private static final String REDIRECT_POSTS_BASE = "redirect:/posts";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "error";

    public PostController(PostService postService) {
        this.postService = postService;
    }

    private String redirectToPostList() {
        return REDIRECT_POSTS_BASE;
    }

    private String redirectToPost(Long postId) {
        return REDIRECT_POSTS_BASE + "/" + postId;
    }

    private String redirectToPostCreate() {
        return REDIRECT_POSTS_BASE + "/new";
    }

    private String redirectToPostEdit(Long postId) {
        return REDIRECT_POSTS_BASE + "/" + postId + "/edit";
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
    public String createPost(
            @Valid @ModelAttribute PostCreateDto createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_ATTRIBUTE,
                    bindingResult.getAllErrors().get(0).getDefaultMessage()
            );
            redirectAttributes.addFlashAttribute("post", createDto);
            return redirectToPostCreate();
        }

        PostResponseDto savedPost = postService.createPost(createDto);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "게시글이 작성되었습니다.");
        return redirectToPost(savedPost.id());
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
            @Valid @ModelAttribute PostUpdateDto updateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERROR_ATTRIBUTE,
                    bindingResult.getAllErrors().get(0).getDefaultMessage()
            );
            return redirectToPostEdit(id);
        }

        postService.updatePost(id, updateDto);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "게시글이 수정되었습니다.");
        return redirectToPost(id);
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "게시글이 삭제되었습니다.");
        return redirectToPostList();
    }
}