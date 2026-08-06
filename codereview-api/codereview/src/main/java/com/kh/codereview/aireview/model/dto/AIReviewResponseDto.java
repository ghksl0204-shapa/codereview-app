package com.kh.codereview.aireview.model.dto;

import com.kh.codereview.aireview.model.vo.AIReview;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AIReviewResponseDto {

    private Long id;
    private Long postId;
    private String status;
    private String content;
    private String model;
    private LocalDateTime createdAt;

    public static AIReviewResponseDto of(AIReview aiReview) {
        return AIReviewResponseDto.builder()
                .id(aiReview.getId())
                .postId(aiReview.getPostId())
                .status(aiReview.getStatus().name())
                .content(aiReview.getContent())
                .model(aiReview.getModel())
                .createdAt(aiReview.getCreatedAt())
                .build();
    }
}
