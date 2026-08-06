package com.kh.codereview.post.model.dto;

import com.kh.codereview.aireview.model.vo.AIReview;
import com.kh.codereview.post.model.vo.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostSummaryDto {

    private Long id;
    private String memberId;
    private String memberNickname;
    private String title;
    private String summary;
    private String language;
    private String category;
    private LocalDateTime createdAt;
    private String aiReviewStatus;

    public static PostSummaryDto of(Post post, AIReview aiReview) {
        return PostSummaryDto.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .title(post.getTitle())
                .summary(post.getSummary())
                .language(post.getLanguage())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .aiReviewStatus(aiReview != null ? aiReview.getStatus().name() : null)
                .build();
    }
}