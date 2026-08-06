package com.kh.codereview.aireview.model.vo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AIReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Lob
    private String content;

    @Column(length = 50)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AIReviewStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AIReview(Long postId) {
        this.postId = postId;
        this.status = AIReviewStatus.PENDING;
    }

    public void complete(String content, String model) {
        this.content = content;
        this.model = model;
        this.status = AIReviewStatus.COMPLETED;
    }

    public void fail() {
        this.status = AIReviewStatus.FAILED;
    }
}