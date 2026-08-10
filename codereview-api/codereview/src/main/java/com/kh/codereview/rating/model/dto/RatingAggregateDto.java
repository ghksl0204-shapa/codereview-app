package com.kh.codereview.rating.model.dto;

import lombok.Getter;

@Getter
public class RatingAggregateDto {

    private final Long commentId;
    private final Double avgKindnessScore;
    private final Double avgAccuracyScore;
    private final Double avgDetailScore;
    private final Long ratingCount;

    public RatingAggregateDto(Long commentId, Double avgKindnessScore, Double avgAccuracyScore,
                               Double avgDetailScore, Long ratingCount) {
        this.commentId = commentId;
        this.avgKindnessScore = avgKindnessScore;
        this.avgAccuracyScore = avgAccuracyScore;
        this.avgDetailScore = avgDetailScore;
        this.ratingCount = ratingCount;
    }
}
