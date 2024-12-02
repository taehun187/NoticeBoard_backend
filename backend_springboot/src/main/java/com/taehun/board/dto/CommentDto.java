package com.taehun.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id; // 댓글 ID
    private String content; // 댓글 내용
    private UserDto writer; // 작성자 정보
    private Long postId; // 게시글 ID (해당 댓글이 속한 게시글)
    private LocalDateTime createdAt; // 댓글 작성일시
}
