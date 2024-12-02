package com.taehun.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taehun.board.dto.PageResponseDto;
import com.taehun.board.dto.PostDto;
import com.taehun.board.message.ResponseCode;
import com.taehun.board.message.ResponseMessage;
import com.taehun.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    // 게시글 생성 (파일 업로드 지원)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseMessage<Void>> createPost(
            @RequestPart("postRequest") String jsonData,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> files) { // List<MultipartFile>로 선언
        try {
            PostDto postDto = objectMapper.readValue(jsonData, PostDto.class);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            postService.createPostWithFiles(postDto, files, username); // List<MultipartFile>를 전달

            return ResponseEntity.ok(
                    ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글이 생성되었습니다.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseMessage.fail(ResponseCode.SERVER_ERROR.getCode(), "게시글 생성 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<PageResponseDto<PostDto>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<PostDto> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글 목록 조회 성공", posts)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseMessage<Long>> getPostCount(@RequestParam(required = false, defaultValue = "all") String type) {
        long count = postService.getPostCount(type);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글 개수 조회 성공", count)
        );
    }

    // 게시글 검색 (키워드)
    @GetMapping("/search/{keyword}")
    public ResponseEntity<ResponseMessage<PageResponseDto<PostDto>>> searchPosts(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<PostDto> posts = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글 검색 성공", posts)
        );
    }

    // 게시글 검색 (태그)
    @GetMapping("/search/tags/{tag}")
    public ResponseEntity<ResponseMessage<PageResponseDto<PostDto>>> searchPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<PostDto> posts = postService.searchPostsByTag(tag, pageable);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "태그로 게시글 검색 성공", posts)
        );
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<PostDto>> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글 조회 성공", post)
        );
    }

    // 게시글 조회수 증가
    @PatchMapping("/views/{id}")
    public ResponseEntity<ResponseMessage<Void>> incrementViews(@PathVariable Long id) {
        postService.incrementViews(id);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "조회수 증가 성공", null)
        );
    }

    // 게시글 좋아요
    @PatchMapping("/likes/{id}")
    public ResponseEntity<ResponseMessage<Void>> likePost(@PathVariable Long id) {
        postService.likePost(id);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "좋아요 추가 성공", null)
        );
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Void>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "게시글 삭제 성공", null)
        );
    }
}

