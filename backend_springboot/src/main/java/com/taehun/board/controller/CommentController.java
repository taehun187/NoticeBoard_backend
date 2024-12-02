package com.taehun.board.controller;

import com.taehun.board.dto.CommentDto;
import com.taehun.board.dto.PageResponseDto;
import com.taehun.board.message.ResponseCode;
import com.taehun.board.message.ResponseMessage;
import com.taehun.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 게시글의 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseMessage<PageResponseDto<CommentDto>>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<CommentDto> comments = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "댓글 조회 성공", comments)
        );
    }

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ResponseMessage<Void>> addComment(@RequestBody CommentDto commentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.addComment(commentDto, username);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "댓글 추가 성공", null)
        );
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<Void>> deleteComment(@PathVariable Long commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.deleteComment(commentId, username);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "댓글 삭제 성공", null)
        );
    }

    // 사용자의 댓글 수 조회
    @GetMapping("/count")
    public ResponseEntity<ResponseMessage<Long>> countCommentsByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        long count = commentService.countCommentsByUser(username);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "댓글 개수 조회 성공", count)
        );
    }
}
