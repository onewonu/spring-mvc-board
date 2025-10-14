package com.mvc.board.springmvcboard.entity;

import com.mvc.board.springmvcboard.exception.InvalidInputException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    protected Post() {}

    public Post(String title, String content) {
        validateTitle(title);
        this.title = title;
        this.content = content;
    }

    public void updateTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw InvalidInputException.of("title", "required");
        }
        if (title.length() > 50) {
            throw InvalidInputException.of("title", "cannot exceed 50 characters");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public List<Comment> getComments() {
        return comments;
    }
}