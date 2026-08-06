package com.kh.codereview.comment.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentListRequestDto {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;

    @Min(value = 0, message = "offset은 0 이상이어야 합니다.")
    private int offset = 0;

    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    @Max(value = 100, message = "limit은 100 이하여야 합니다.")
    private int limit = 20;
}