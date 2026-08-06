package com.kh.codereview.rating.model.vo;

import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.member.model.vo.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "rating",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_rating_comment_member",
        columnNames = {"comment_id", "member_id"}
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "kindness_score", nullable = false)
    private Integer kindnessScore;

    @Column(name = "accuracy_score", nullable = false)
    private Integer accuracyScore;

    @Column(name = "detail_score", nullable = false)
    private Integer detailScore;

    @Column(name = "comment_text", length = 500)
    private String commentText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Rating(Comment comment, Member member, Integer kindnessScore,
                  Integer accuracyScore, Integer detailScore, String commentText) {
        this.comment = comment;
        this.member = member;
        this.kindnessScore = kindnessScore;
        this.accuracyScore = accuracyScore;
        this.detailScore = detailScore;
        this.commentText = commentText;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}