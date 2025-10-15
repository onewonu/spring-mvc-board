package com.mvc.board.springmvcboard;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.CommentResponseDto;
import com.mvc.board.springmvcboard.dto.CommentUpdateDto;
import com.mvc.board.springmvcboard.entity.Comment;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.exception.EntityNotFoundException;
import com.mvc.board.springmvcboard.exception.InvalidInputException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
                    .isInstanceOf(InvalidInputException.class);

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
                    .isInstanceOf(InvalidInputException.class);

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
                    .isInstanceOf(InvalidInputException.class);

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
                    .isInstanceOf(EntityNotFoundException.class);

            then(postRepository).should().findById(nonExistentPostId);
            then(commentRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {

        @Test
        @DisplayName("성공")
        void updateCommentSuccess() {
            // given
            Long commentId = 1L;
            String originalContent = "원래 댓글 내용";
            String newContent = "수정된 댓글 내용";

            Post realPost = new Post("게시글 제목", "게시글 내용");
            Comment realComment = new Comment(originalContent, realPost);

            CommentUpdateDto updateDto = CommentUpdateDto.of(newContent);

            given(commentRepository.findById(commentId)).willReturn(Optional.of(realComment));

            // when
            CommentResponseDto result = commentService.updateComment(commentId, updateDto);

            // then
            assertThat(realComment.getContent()).isEqualTo(newContent);

            assertThat(result.content()).isEqualTo(newContent);
            assertThat(result.postId()).isEqualTo(realPost.getId());

            then(commentRepository).should().findById(commentId);
        }

        @Test
        @DisplayName("null DTO로 댓글 수정 시 예외 발생")
        void updateCommentWithNullDtoThrowsException() {
            // when, then
            assertThatThrownBy(() -> commentService.updateComment(1L, null))
                    .isInstanceOf(InvalidInputException.class);

            then(commentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 내용으로 댓글 수정 시 예외 발생")
        void updateCommentWithEmptyContentThrowsException() {
            // given
            Long commentId = 1L;
            String originalContent = "원래 댓글 내용";

            Post realPost = new Post("게시글 제목", "게시글 내용");
            Comment realComment = new Comment(originalContent, realPost);
            CommentUpdateDto emptyContentDto = CommentUpdateDto.of("");

            given(commentRepository.findById(commentId)).willReturn(Optional.of(realComment));

            // when, then
            assertThatThrownBy(() -> commentService.updateComment(commentId, emptyContentDto))
                    .isInstanceOf(InvalidInputException.class);

            assertThat(realComment.getContent()).isEqualTo(originalContent);

            then(commentRepository).should().findById(commentId);
        }

        @Test
        @DisplayName("공백만 있는 내용으로 댓글 수정 시 예외 발생")
        void updateCommentWithWhitespaceContentThrowsException() {
            // given
            Long commentId = 1L;
            String originalContent = "원래 댓글 내용";

            Post realPost = new Post("게시글 제목", "게시글 내용");
            Comment realComment = new Comment(originalContent, realPost);
            CommentUpdateDto whitespaceContentDto = CommentUpdateDto.of("   ");

            given(commentRepository.findById(commentId)).willReturn(Optional.of(realComment));

            // when, then
            assertThatThrownBy(() -> commentService.updateComment(commentId, whitespaceContentDto))
                    .isInstanceOf(InvalidInputException.class);

            assertThat(realComment.getContent()).isEqualTo(originalContent);

            then(commentRepository).should().findById(commentId);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 수정 시 예외 발생")
        void updateCommentNotFoundThrowsException() {
            // given
            Long nonExistentCommentId = 999L;
            CommentUpdateDto updateDto = CommentUpdateDto.of("수정된 댓글 내용");

            given(commentRepository.findById(nonExistentCommentId)).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> commentService.updateComment(nonExistentCommentId, updateDto))
                    .isInstanceOf(EntityNotFoundException.class);

            then(commentRepository).should().findById(nonExistentCommentId);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {

        @Test
        @DisplayName("성공")
        void deleteCommentSuccess() {
            // given
            Long commentId = 1L;
            given(commentRepository.existsById(commentId)).willReturn(true);

            // when
            commentService.deleteComment(commentId);

            // then
            then(commentRepository).should().existsById(commentId);
            then(commentRepository).should().deleteById(commentId);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 삭제 시 예외 발생")
        void deleteCommentNotFoundThrowsException() {
            // given
            Long nonExistentCommentId = 999L;
            given(commentRepository.existsById(nonExistentCommentId)).willReturn(false);

            // when, then
            assertThatThrownBy(() -> commentService.deleteComment(nonExistentCommentId))
                    .isInstanceOf(EntityNotFoundException.class);

            then(commentRepository).should().existsById(nonExistentCommentId);
            then(commentRepository).should(never()).deleteById(anyLong());
        }
    }
}