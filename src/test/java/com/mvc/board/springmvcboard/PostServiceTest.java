package com.mvc.board.springmvcboard;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostDetailDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.dto.PostUpdateDto;
import com.mvc.board.springmvcboard.entity.Post;
import com.mvc.board.springmvcboard.repository.PostRepository;
import com.mvc.board.springmvcboard.service.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Nested
    @DisplayName("게시글 목록 조회")
    class GetAllPosts {

        @Test
        @DisplayName("성공")
        void getAllPostsSuccess() {
            // given
            Post post1 = new Post("제목1", "내용1");
            Post post2 = new Post("제목2", "내용2");

            for (int i = 0; i < 10; i++) post1.incrementViewCount();
            for (int i = 0; i < 5; i++) post2.incrementViewCount();

            given(postRepository.findAll()).willReturn(Arrays.asList(post1, post2));

            // when
            List<PostResponseDto> result = postService.getAllPosts();

            // then
            assertThat(result).hasSize(2);

            PostResponseDto firstPost = result.get(0);
            assertThat(firstPost.title()).isEqualTo("제목1");
            assertThat(firstPost.content()).isNull();
            assertThat(firstPost.viewCount()).isEqualTo(10);
            assertThat(firstPost.commentCount()).isZero();

            PostResponseDto secondPost = result.get(1);
            assertThat(secondPost.title()).isEqualTo("제목2");
            assertThat(secondPost.viewCount()).isEqualTo(5);
            assertThat(secondPost.commentCount()).isZero();

            then(postRepository).should().findAll();
        }

        @Test
        @DisplayName("빈 게시글 목록 조회")
        void getAllPostsEmptyList() {
            // given
            given(postRepository.findAllWithComments()).willReturn(List.of());

            // when
            List<PostResponseDto> result = postService.getAllPosts();

            // then
            assertThat(result).isEmpty();
            then(postRepository).should().findAllWithComments();
        }
    }

    @Nested
    @DisplayName("게시글 생성")
    class CreatePost {
        @Test
        @DisplayName("정상적인 게시글 생성")
        void createPostSuccess() {
            // given
            String title = "새 게시글";
            String content = "새 내용";

            PostCreateDto createDto = PostCreateDto.of(title, content);

            Post realPost = new Post(title, content);
            given(postRepository.save(any(Post.class))).willReturn(realPost);

            // when
            PostResponseDto result = postService.createPost(createDto);

            // then
            assertThat(result.title()).isEqualTo(title);
            assertThat(result.content()).isEqualTo(content);
            assertThat(result.viewCount()).isZero();
            assertThat(result.commentCount()).isZero();

            then(postRepository).should().save(any(Post.class));
        }

        @Test
        @DisplayName("null DTO로 게시글 생성 시 예외 발생")
        void createPostWithNullDtoThrowsException() {
            // when, then
            assertThatThrownBy(() -> postService.createPost(null))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 제목으로 게시글 생성 시 예외 발생")
        void createPostWithEmptyTitleThrowsException() {
            // given
            String content = "내용";

            PostCreateDto emptyTitleDto = PostCreateDto.of("", content);

            // when, then
            assertThatThrownBy(() -> postService.createPost(emptyTitleDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("50자 초과 제목으로 게시글 생성 시 예외 발생")
        void createPostWithTooLongTitleThrowsException() {
            // given
            String longTitle = "a".repeat(51);
            PostCreateDto longTitleDto = PostCreateDto.of(longTitle, "내용");

            // when, then
            assertThatThrownBy(() -> postService.createPost(longTitleDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회")
    class GetPostDetail {

        @Test
        @DisplayName("성공 - 조회수 증가 확인")
        void getPostDetailSuccessIncrementViewCount() {
            // given
            String title = "제목";
            String content = "내용";

            Post realPost = new Post(title, content);
            given(postRepository.findById(1L)).willReturn(Optional.of(realPost));

            // when
            PostDetailDto result = postService.getPostDetail(1L);

            // then
            assertThat(result.title()).isEqualTo(title);
            assertThat(result.content()).isEqualTo(content);
            assertThat(result.viewCount()).isEqualTo(1);
            assertThat(result.comments()).isEmpty();

            assertThat(realPost.getViewCount()).isEqualTo(1);

            then(postRepository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
        void getPostDetailNotFoundThrowsException() {
            // given
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> postService.getPostDetail(999L))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).should().findById(999L);
        }

        @Test
        @DisplayName("여러 번 조회 시 조회수 누적 증가")
        void getPostDetailMultipleViewsIncrement() {
            // given
            Post realPost = new Post("제목", "내용");
            given(postRepository.findById(1L)).willReturn(Optional.of(realPost));

            // when
            postService.getPostDetail(1L);
            postService.getPostDetail(1L);
            PostDetailDto result = postService.getPostDetail(1L);

            // then
            assertThat(result.viewCount()).isEqualTo(3);
            assertThat(realPost.getViewCount()).isEqualTo(3);

            then(postRepository).should(times(3)).findById(1L);
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class UpdatePost {

        @Test
        @DisplayName("성공")
        void updatePostSuccess() {
            // given
            Post realPost = new Post("원래 제목", "원래 내용");

            String newTitle = "수정된 제목";
            String newContent = "수정된 내용";

            PostUpdateDto updateDto = PostUpdateDto.of(newTitle, newContent);

            given(postRepository.findById(1L)).willReturn(Optional.of(realPost));

            // when
            PostResponseDto result = postService.updatePost(1L, updateDto);

            // then
            assertThat(realPost.getTitle()).isEqualTo(newTitle);
            assertThat(realPost.getContent()).isEqualTo(newContent);

            assertThat(result.title()).isEqualTo(newTitle);
            assertThat(result.content()).isEqualTo(newContent);

            then(postRepository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 수정 시 예외 발생")
        void updatePostNotFoundThrowsException() {
            // given
            given(postRepository.findById(999L)).willReturn(Optional.empty());
            PostUpdateDto updateDto = PostUpdateDto.of("수정된 제목", "수정된 내용");

            // when, then
            assertThatThrownBy(() -> postService.updatePost(999L, updateDto))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).should().findById(999L);
        }

        @Test
        @DisplayName("null DTO로 게시글 수정 시 예외 발생")
        void updatePostWithNullDtoThrowsException() {
            // when, then
            assertThatThrownBy(() -> postService.updatePost(1L, null))
                    .isInstanceOf(IllegalArgumentException.class);

            then(postRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 제목으로 게시글 수정 시 예외 발생")
        void updatePostWithEmptyTitleThrowsException() {
            // given
            Post realPost = new Post("원래 제목", "원래 내용");
            PostUpdateDto emptyTitleDto = PostUpdateDto.of("", "수정된 내용");

            given(postRepository.findById(1L)).willReturn(Optional.of(realPost));

            // when, then
            assertThatThrownBy(() -> postService.updatePost(1L, emptyTitleDto))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(realPost.getTitle()).isEqualTo("원래 제목");
            assertThat(realPost.getContent()).isEqualTo("원래 내용");
        }

        @Test
        @DisplayName("50자 초과 제목으로 게시글 수정 시 예외 발생")
        void updatePostWithTooLongTitleThrowsException() {
            // given
            Post realPost = new Post("원래 제목", "원래 내용");

            String longTitle = "a".repeat(51);
            PostUpdateDto longTitleDto = PostUpdateDto.of(longTitle, "수정된 내용");

            given(postRepository.findById(1L)).willReturn(Optional.of(realPost));

            // when, then
            assertThatThrownBy(() -> postService.updatePost(1L, longTitleDto))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(realPost.getTitle()).isEqualTo("원래 제목");
            assertThat(realPost.getContent()).isEqualTo("원래 내용");
        }
    }
}