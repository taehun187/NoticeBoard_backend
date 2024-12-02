package com.taehun.board.service;

import com.taehun.board.dto.PageResponseDto;
import com.taehun.board.dto.PostDto;
import com.taehun.board.dto.UserDto;
import com.taehun.board.entity.Post;
import com.taehun.board.entity.Tag;
import com.taehun.board.entity.User;
import com.taehun.board.repository.PostRepository;
import com.taehun.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 게시글 생성 (파일 업로드 포함)
    public void createPostWithFiles(PostDto postDto, List<MultipartFile> files, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .type(postDto.getType() != null ? postDto.getType() : "default")
                .isPrivate(postDto.getIsPrivate() != null ? postDto.getIsPrivate() : false)
                .isCommentsBlocked(postDto.getIsCommentsBlocked() != null ? postDto.getIsCommentsBlocked() : false)
                .writer(user)
                .build();

        // 태그 추가
        postDto.getTags().forEach(tagName -> {
            Tag tag = Tag.builder().name(tagName).build();
            post.addTag(tag);
        });

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            StringBuilder filePaths = new StringBuilder();
            files.forEach(file -> {
                try {
                    String fileUrl = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
                    filePaths.append(fileUrl).append(";"); // 파일 경로를 세미콜론(;)으로 구분
                } catch (Exception e) {
                    throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
                }
            });
            post.setFilePath(filePaths.toString()); // 전체 파일 경로 저장
        }

        postRepository.save(post);
    }

    // 게시글 목록 조회 (비공개 제외)
    public PageResponseDto<PostDto> getPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByIsPrivateFalse(pageable);

        return PageResponseDto.<PostDto>builder()
                .content(postPage.getContent().stream().map(this::convertToDto).toList())
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .isFirst(postPage.isFirst())
                .isLast(postPage.isLast())
                .build();
    }

    // 특정 게시글 조회
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return convertToDto(post);
    }

    // 조회수 증가
    public void incrementViews(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementViews();
        postRepository.save(post);
    }

    // 좋아요 증가
    public void likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementLikes();
        postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        postRepository.delete(post); // 연관 댓글도 함께 삭제
    }

    // 게시글 개수 조회
    public long getPostCount(String type) {
        if ("all".equalsIgnoreCase(type)) {
            return postRepository.count(); // 모든 게시글 개수
        }
        return postRepository.countByType(type); // 특정 유형의 게시글 개수
    }

    // 키워드 검색
    public PageResponseDto<PostDto> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return convertToPageResponse(posts);
    }

    // 태그 검색
    public PageResponseDto<PostDto> searchPostsByTag(String tag, Pageable pageable) {
        Page<Post> posts = postRepository.findByTagsName(tag, pageable);
        return convertToPageResponse(posts);
    }

    // Page 객체를 PageResponseDto로 변환
    private PageResponseDto<PostDto> convertToPageResponse(Page<Post> postPage) {
        return PageResponseDto.<PostDto>builder()
                .content(postPage.getContent().stream().map(this::convertToDto).toList())
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .isFirst(postPage.isFirst())
                .isLast(postPage.isLast())
                .build();
    }

    // Post 엔티티를 PostDto로 변환
    private PostDto convertToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getType())
                .writer(post.getWriter() != null ?
                        UserDto.builder().username(post.getWriter().getUsername()).build() :
                        UserDto.builder().username("작성자 없음").build())
                .tags(post.getTags() != null ? post.getTags().stream().map(Tag::getName).toList() : List.of())
                .likes(post.getLikes() != null ? post.getLikes() : 0)
                .views(post.getViews() != null ? post.getViews() : 0)
                .isPrivate(post.getIsPrivate())
                .isCommentsBlocked(post.getIsCommentsBlocked())
                .createdAt(post.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }


}


