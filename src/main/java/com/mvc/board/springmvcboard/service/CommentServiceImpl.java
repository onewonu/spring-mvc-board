package com.mvc.board.springmvcboard.service;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;
import com.mvc.board.springmvcboard.entity.Comment;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.exception.EntityNotFoundException;
import com.mvc.board.springmvcboard.exception.InvalidInputException;
import com.mvc.board.springmvcboard.repository.CommentRepository;
import com.mvc.board.springmvcboard.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

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
            throw new InvalidInputException("CommentCreateDto cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> EntityNotFoundException.of("Post", postId));

        Comment comment = new Comment(createDto.content(), post);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDto.from(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentUpdateDto updateDto) {
        if (updateDto == null) {
            throw new InvalidInputException("CommentUpdateDto cannot be null");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> EntityNotFoundException.of("Comment", commentId));

        comment.updateContent(updateDto.content());

        return CommentResponseDto.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw EntityNotFoundException.of("Comment", commentId);
        }

        commentRepository.deleteById(commentId);
    }
}