package com.taehun.board.repository;

import com.taehun.board.entity.Post;
import com.taehun.board.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByIsPrivateFalse(Pageable pageable); // 비공개 제외 페이징 조회

    long countByWriter(User writer); // 특정 유저의 게시글 개수 조회

    List<Post> findAllByWriter(User writer); // 특정 유저의 모든 게시글 조회

    long countByType(String type); // 특정 타입의 게시글 개수 조회

    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable); // 키워드 검색

    Page<Post> findByTagsName(String tag, Pageable pageable); // 태그 검색
}
