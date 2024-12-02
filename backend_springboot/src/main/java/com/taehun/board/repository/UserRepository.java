package com.taehun.board.repository;


import com.taehun.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);          // 이메일로 유저 조회
    Optional<User> findByUsername(String username);    // 유저명으로 유저 조회
    boolean existsByEmailOrUsername(String email, String username); // 이메일 또는 유저명 중복 체크
}

