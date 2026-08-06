package com.kh.codereview.rating.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RatingListResponseDto {

    private List<RatingResponseDto> items;
    private long totalCount;
    private Double averageKindnessScore;
    private Double averageAccuracyScore;
    private Double averageDetailScore;

    public static RatingListResponseDto of(List<RatingResponseDto> items) {
        if (items.isEmpty()) {
            return RatingListResponseDto.builder()
                    .items(items)
                    .totalCount(0)
                    .averageKindnessScore(null)
                    .averageAccuracyScore(null)
                    .averageDetailScore(null)
                    .build();
        }

        double avgKindness = items.stream().mapToInt(RatingResponseDto::getKindnessScore).average().orElse(0);
        double avgAccuracy = items.stream().mapToInt(RatingResponseDto::getAccuracyScore).average().orElse(0);
        double avgDetail = items.stream().mapToInt(RatingResponseDto::getDetailScore).average().orElse(0);

        return RatingListResponseDto.builder()
                .items(items)
                .totalCount(items.size())
                .averageKindnessScore(round(avgKindness))
                .averageAccuracyScore(round(avgAccuracy))
                .averageDetailScore(round(avgDetail))
                .build();
    }

    private static double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}