package com.kh.codereview.post.model.dto;

import com.kh.codereview.aireview.model.vo.AIReview;
import com.kh.codereview.common.util.KstDateTime;
import com.kh.codereview.post.model.vo.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class PostResponseDto {

    private Long id;
    private String memberId;
    private String memberNickname;
    private String title;
    private String summary;
    private String codeContent;
    private String language;
    private String category;
    private String reviewFocus;
    private OffsetDateTime createdAt;
    private String aiReviewStatus;
    private String aiReviewContent;

    public static PostResponseDto of(Post post, AIReview aiReview) {
        return PostResponseDto.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .title(post.getTitle())
                .summary(post.getSummary())
                .codeContent(post.getCodeContent())
                .language(post.getLanguage())
                .category(post.getCategory())
                .reviewFocus(post.getReviewFocus())
                .createdAt(KstDateTime.from(post.getCreatedAt()))
                .aiReviewStatus(aiReview != null ? aiReview.getStatus().name() : null)
                .aiReviewContent(aiReview != null ? aiReview.getContent() : null)
                .build();
    }
}