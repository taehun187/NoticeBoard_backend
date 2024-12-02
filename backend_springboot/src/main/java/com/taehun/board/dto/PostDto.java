package com.taehun.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String type; // 게시글 유형
    private UserDto writer; // 작성자 정보
    private List<CommentDto> comments; // 댓글 리스트
    private Integer likes; // 좋아요 수
    private Integer views; // 조회수
    private Boolean isPrivate; // 비공개 여부
    private Boolean isCommentsBlocked; // 댓글 차단 여부
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시
    private List<String> tags; // 태그 이름 리스트
}
