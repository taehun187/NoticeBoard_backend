package com.taehun.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 ID

    @Column(nullable = false, length = 100)
    private String title; // 게시글 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer; // 작성자 정보

    @Column(columnDefinition = "TEXT")
    private String filePath;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>(); // 댓글 리스트

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>(); // 태그 리스트

    @Column(nullable = false)
    private Integer likes; // 좋아요 수

    @Column(nullable = false)
    private Integer views; // 조회수

    @Column(nullable = false)
    private Boolean isPrivate; // 비공개 여부

    @Column(nullable = false)
    private Boolean isCommentsBlocked; // 댓글 차단 여부

    @Column(length = 50)
    private String type; // 게시글 유형

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt; // 생성일자

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt; // 수정일자

    // 조회수 증가
    public void incrementViews() {
        this.views = (this.views == null ? 0 : this.views) + 1;
    }

    // 좋아요 증가
    public void incrementLikes() {
        this.likes = (this.likes == null ? 0 : this.likes) + 1;
    }

    // 댓글 차단 상태 토글
    public void toggleCommentsBlocked() {
        this.isCommentsBlocked = !this.isCommentsBlocked;
    }

    // 태그 추가
    public void addTag(Tag tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    // 태그 삭제
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    // 비공개 상태 확인
    public boolean isPublic() {
        return !this.isPrivate;
    }

    @PrePersist
    public void prePersist() {
        this.likes = (this.likes == null) ? 0 : this.likes;
        this.views = (this.views == null) ? 0 : this.views;
        this.isPrivate = (this.isPrivate == null) ? false : this.isPrivate;
        this.isCommentsBlocked = (this.isCommentsBlocked == null) ? false : this.isCommentsBlocked;
        this.type = (this.type == null) ? "default" : this.type;
    }
}
