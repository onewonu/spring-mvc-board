package com.mvc.board.springmvcboard;

import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


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
}