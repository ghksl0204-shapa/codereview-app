package com.kh.codereview.rating.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RatingSummaryDto {

    private long ratingCount;
    private Double averageKindnessScore;
    private Double averageAccuracyScore;
    private Double averageDetailScore;
    private RatingResponseDto myRating;

    public static RatingSummaryDto empty() {
        return RatingSummaryDto.builder()
                .ratingCount(0)
                .averageKindnessScore(null)
                .averageAccuracyScore(null)
                .averageDetailScore(null)
                .myRating(null)
                .build();
    }
}
