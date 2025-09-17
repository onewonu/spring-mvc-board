package com.mvc.board.springmvcboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected Comment() {}

    public Comment(String content, Post post) {
        validateContent(content);
        this.content = content;
        this.post = post;
    }

    public void updateContent(String newContent) {
        validateContent(newContent);
        this.content = newContent;
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("The content is required");
        }
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Post getPost() {
        return post;
    }
}