package com.kh.codereview.comment.model.dto;

import com.kh.codereview.comment.model.vo.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {

    private Long id;
    private Long postId;
    private String memberId;
    private String memberNickname;
    private String content;
    private Long parentCommentId;
    private String status;
    private LocalDateTime createdAt;

    public static CommentResponseDto of(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .memberId(comment.getMember().getId())
                .memberNickname(comment.getMember().getNickname())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}