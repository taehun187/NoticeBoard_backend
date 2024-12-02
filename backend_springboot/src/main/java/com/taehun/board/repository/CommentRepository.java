package com.taehun.board.repository;

import com.taehun.board.entity.Comment;
import com.taehun.board.entity.Post;
import com.taehun.board.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable); // 게시글 댓글 조회
    long countByWriter(User writer); // 특정 유저 댓글 수 조회
}
