package com.mvc.board.springmvcboard;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;
import com.mvc.board.springmvcboard.entity.Comment;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.repository.CommentRepository;
import com.mvc.board.springmvcboard.repository.PostRepository;
import com.mvc.board.springmvcboard.service.CommentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    @DisplayName("댓글 생성")
    class CreateComment {

        @Test
        @DisplayName("성공")
        void createCommentSuccess() {
            // given
            Long postId = 1L;
            String content = "새 댓글 내용";

            CommentCreateDto createDto = CommentCreateDto.of(content);
            Post realPost = new Post("게시글 제목", "게시글 내용");
            Comment realComment = new Comment(content, realPost);

            given(postRepository.findById(postId)).willReturn(Optional.of(realPost));
            given(commentRepository.save(any(Comment.class))).willReturn(realComment);

            // when
            CommentResponseDto result = commentService.createComment(postId, createDto);

            // then
            assertThat(result.content()).isEqualTo(content);
            assertThat(result.postId()).isEqualTo(realPost.getId());

            then(postRepository).should().findById(postId);
            then(commentRepository).should().save(any(Comment.class));
        }

        @Test
        @DisplayName("null DTO로 댓글 생성 시 예외 발생")
        void createCommentWithNullDtoThrowsException() {
            // when, then
            assertThatThrownBy(() -> commentService.createComment(1L, null))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).shouldHaveNoInteractions();
            then(commentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 내용으로 댓글 생성 시 예외 발생")
        void createCommentWithEmptyContentThrowsException() {
            // given
            Long postId = 1L;

            CommentCreateDto emptyContentDto = CommentCreateDto.of("");

            Post realPost = new Post("게시글 제목", "게시글 내용");
            given(postRepository.findById(postId)).willReturn(Optional.of(realPost));

            // when, then
            assertThatThrownBy(() -> commentService.createComment(postId, emptyContentDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).should().findById(postId);
            then(commentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("공백만 있는 내용으로 댓글 생성 시 예외 발생")
        void createCommentWithWhitespaceContentThrowsException() {
            // given
            Long postId = 1L;

            CommentCreateDto whitespaceContentDto = CommentCreateDto.of("   ");

            Post realPost = new Post("게시글 제목", "게시글 내용");
            given(postRepository.findById(postId)).willReturn(Optional.of(realPost));

            // when, then
            assertThatThrownBy(() -> commentService.createComment(postId, whitespaceContentDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).should().findById(postId);
            then(commentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 댓글 생성 시 예외 발생")
        void createCommentWithNonExistentPostThrowsException() {
            // given
            Long nonExistentPostId = 999L;
            CommentCreateDto createDto = CommentCreateDto.of("댓글 내용");

            given(postRepository.findById(nonExistentPostId)).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> commentService.createComment(nonExistentPostId, createDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).should().findById(nonExistentPostId);
            then(commentRepository).shouldHaveNoInteractions();
        }
    }

}