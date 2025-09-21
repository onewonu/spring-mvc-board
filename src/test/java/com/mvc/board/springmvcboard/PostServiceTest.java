package com.mvc.board.springmvcboard;

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
}
