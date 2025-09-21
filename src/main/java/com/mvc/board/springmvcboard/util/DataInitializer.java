package com.mvc.board.springmvcboard.util;

import com.mvc.board.springmvcboard.dto.CommentCreateDto;
import com.mvc.board.springmvcboard.dto.PostCreateDto;
import com.mvc.board.springmvcboard.dto.PostResponseDto;
import com.mvc.board.springmvcboard.service.CommentService;
import com.mvc.board.springmvcboard.service.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    private final PostService postService;
    private final CommentService commentService;

    public DataInitializer(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @Override
    public void run(String... args) {
        if (shouldInitializeData()) {
            System.out.println("더미 데이터 생성을 시작합니다.");
            createDummyData();
            System.out.println("더미 데이터 생성이 완료되었습니다.");
        } else {
            System.out.println("데이터가 이미 존재 합니다. 더미 데이터를 생성하지 않습니다.");
        }
    }

    private boolean shouldInitializeData() {
        return postService.getAllPosts().isEmpty();
    }

    private void createDummyData() {
        PostResponseDto post1 = postService.createPost(
                PostCreateDto.of("Spring Boot 시작하기",
                        "Spring Boot는 스프링 기반의 애플리케이션을 쉽게 개발할 수 있도록 도와주는 프레임워크입니다.")
        );

        PostResponseDto post2 = postService.createPost(
                PostCreateDto.of("JPA 기초 개념",
                        "JPA는 Java Persistence API의 줄임말로, 자바에서 ORM을 사용하기 위한 표준 인터페이스입니다.")
        );

        PostResponseDto post3 = postService.createPost(
                PostCreateDto.of("Docker로 MySQL 띄우기",
                        "Docker를 사용하여 MySQL 컨테이너를 실행하는 방법을 알아봅시다.")
        );

        PostResponseDto post4 = postService.createPost(
                PostCreateDto.of("Thymeleaf 템플릿 엔진",
                        "Thymeleaf는 서버 사이드 Java 템플릿 엔진입니다. HTML과 자연스럽게 통합됩니다.")
        );

        PostResponseDto post5 = postService.createPost(
                PostCreateDto.of("RESTful API 설계 원칙",
                        "REST 아키텍처 스타일을 따르는 API 설계의 핵심 원칙들을 살펴봅시다.")
        );

        PostResponseDto post6 = postService.createPost(
                PostCreateDto.of("클린 코드 작성법",
                        "가독성 좋고 유지보수하기 쉬운 코드를 작성하는 방법에 대해 알아봅시다.")
        );

        PostResponseDto post7 = postService.createPost(
                PostCreateDto.of("Git 브랜치 전략",
                        "효율적인 협업을 위한 Git 브랜치 전략과 워크플로우를 소개합니다.")
        );

        createCommentsForPost(post1.id());
        createCommentsForPost(post2.id());
        createCommentsForPost(post3.id());
        createCommentsForPost(post4.id());
        createCommentsForPost(post5.id());
        createCommentsForPost(post6.id());
        createCommentsForPost(post7.id());
    }

    private void createCommentsForPost(Long postId) {
        String[] comments = {
                "정말 유용한 정보네요! 감사합니다.",
                "저도 이 부분이 궁금했는데 도움이 되었습니다.",
                "실무에서 바로 적용해볼 수 있을 것 같아요.",
                "추가적으로 궁금한 내용이 있는데 답변 부탁드립니다.",
                "좋은 글 잘 읽었습니다. 계속해서 좋은 글 부탁드려요!",
                "이해하기 쉽게 설명해주셔서 감사합니다.",
                "실제 코드 예제가 있으면 더 좋을 것 같아요."
        };

        int commentCount = (int) (Math.random() * 3) + 2;

        for (int i = 0; i < commentCount; i++) {
            int randomIndex = (int) (Math.random() * comments.length);
            commentService.createComment(postId,
                    CommentCreateDto.of(comments[randomIndex]));
        }
    }
}