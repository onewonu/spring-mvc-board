package com.mvc.board.springmvcboard.controller;

import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
