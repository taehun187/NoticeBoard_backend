package com.taehun.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content; // 데이터 리스트 (PostDto, CommentDto 등)
    private int currentPage; // 현재 페이지 번호
    private int totalPages;  // 전체 페이지 수
    private long totalElements; // 전체 데이터 수
    private boolean isFirst; // 첫 번째 페이지 여부
    private boolean isLast;  // 마지막 페이지 여부
}
