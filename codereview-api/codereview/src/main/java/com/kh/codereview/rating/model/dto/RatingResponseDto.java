package com.kh.codereview.rating.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RatingResponseDto {
    private Long id;
    private Long commentId;
    private String memberId;
    private String memberNickname;
    private Integer kindnessScore;
    private Integer accuracyScore;
    private Integer detailScore;
    private String commentText;
    private LocalDateTime createdAt;
}