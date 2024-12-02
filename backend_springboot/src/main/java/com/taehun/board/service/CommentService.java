package com.taehun.board.service;

import com.taehun.board.dto.CommentDto;
import com.taehun.board.dto.PageResponseDto;
import com.taehun.board.dto.UserDto;
import com.taehun.board.entity.Comment;
import com.taehun.board.entity.Post;
import com.taehun.board.entity.User;
import com.taehun.board.repository.CommentRepository;
import com.taehun.board.repository.PostRepository;
import com.taehun.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글의 댓글 조회
    public PageResponseDto<CommentDto> getCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Page<Comment> commentPage = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);

        return PageResponseDto.<CommentDto>builder()
                .content(commentPage.getContent().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()))
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .isFirst(commentPage.isFirst())
                .isLast(commentPage.isLast())
                .build();
    }

    // 댓글 작성
    public void addComment(CommentDto commentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .writer(user)
                .post(post)
                .build();

        commentRepository.save(comment);
    }

    // 댓글 삭제 (물리적 삭제)
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        if (!comment.getWriter().getUsername().equals(username)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    // 사용자의 댓글 수 조회
    public long countCommentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return commentRepository.countByWriter(user);
    }

    // Comment 엔티티를 CommentDto로 변환
    private CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(UserDto.builder()
                        .username(comment.getWriter().getUsername())
                        .email(comment.getWriter().getEmail())
                        .build())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
