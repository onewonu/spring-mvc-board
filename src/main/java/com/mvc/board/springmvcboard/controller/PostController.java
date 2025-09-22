package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

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
            postService.createPost(createDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
            return "redirect:/posts";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("post", createDto);
            return "redirect:/posts/new";
        }
    }
}
