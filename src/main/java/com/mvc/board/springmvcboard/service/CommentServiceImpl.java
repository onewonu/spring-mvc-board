package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;
import com.mvc.board.springmvcboard.entity.Comment;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.repository.CommentRepository;
import com.mvc.board.springmvcboard.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentCreateDto createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException("CommentCreateDto cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        Comment comment = new Comment(createDto.content(), post);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDto.from(savedComment);
    }
}
