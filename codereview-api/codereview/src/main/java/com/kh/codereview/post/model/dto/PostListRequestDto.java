package com.kh.codereview.post.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListRequestDto {

    @Min(value = 0, message = "offset은 0 이상이어야 합니다.")
    private int offset = 0;

    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    @Max(value = 100, message = "limit은 100 이하여야 합니다.")
    private int limit = 10;

    private String category;
    private String language;
    private String keyword;
}