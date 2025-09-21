package com.mvc.board.springmvcboard.repository;

import com.mvc.board.springmvcboard.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
