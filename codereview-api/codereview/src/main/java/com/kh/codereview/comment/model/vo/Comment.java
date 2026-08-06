package com.kh.codereview.comment.model.vo;

import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.post.model.vo.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comment(Post post, Member member, String content, Comment parentComment) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.parentComment = parentComment;
        this.status = CommentStatus.NORMAL;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }

    public boolean isDeleted() {
        return this.status == CommentStatus.DELETED;
    }

    public boolean isReply() {
        return this.parentComment != null;
    }

    public boolean isOwnedBy(String memberId) {
        return this.member.getId().equals(memberId);
    }

    public Long getParentCommentId() {
        return this.parentComment != null ? this.parentComment.getId() : null;
    }

    public Long getPostId() {
        return this.post.getId();
    }
}
