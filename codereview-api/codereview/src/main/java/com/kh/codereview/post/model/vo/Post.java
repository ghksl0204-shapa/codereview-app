package com.kh.codereview.post.model.vo;

import com.kh.codereview.member.model.vo.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String summary;

    @Lob
    @Column(name = "code_content", nullable = false)
    private String codeContent;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "review_focus", length = 500)
    private String reviewFocus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Post(Member member, String title, String summary, String codeContent,
                String language, String category, String reviewFocus) {
        this.member = member;
        this.title = title;
        this.summary = summary;
        this.codeContent = codeContent;
        this.language = language;
        this.category = category;
        this.reviewFocus = reviewFocus;
        this.status = PostStatus.ACTIVE;
    }

    public void update(String title, String summary, String codeContent,
                        String language, String category, String reviewFocus) {
        this.title = title;
        this.summary = summary;
        this.codeContent = codeContent;
        this.language = language;
        this.category = category;
        this.reviewFocus = reviewFocus;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    public boolean isOwnedBy(String memberId) {
        return this.member.getId().equals(memberId);
    }
}