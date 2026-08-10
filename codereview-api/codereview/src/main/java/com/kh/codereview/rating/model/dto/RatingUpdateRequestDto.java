package com.kh.codereview.rating.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingUpdateRequestDto {

    @NotNull(message = "친절도 점수는 필수입니다.")
    @Min(value = 1, message = "친절도 점수는 1~5 사이여야 합니다.")
    @Max(value = 5, message = "친절도 점수는 1~5 사이여야 합니다.")
    private Integer kindnessScore;

    @NotNull(message = "정확도 점수는 필수입니다.")
    @Min(value = 1, message = "정확도 점수는 1~5 사이여야 합니다.")
    @Max(value = 5, message = "정확도 점수는 1~5 사이여야 합니다.")
    private Integer accuracyScore;

    @NotNull(message = "상세도 점수는 필수입니다.")
    @Min(value = 1, message = "상세도 점수는 1~5 사이여야 합니다.")
    @Max(value = 5, message = "상세도 점수는 1~5 사이여야 합니다.")
    private Integer detailScore;

    @Size(max = 500, message = "평가 코멘트는 500자를 초과할 수 없습니다.")
    private String commentText;
}
