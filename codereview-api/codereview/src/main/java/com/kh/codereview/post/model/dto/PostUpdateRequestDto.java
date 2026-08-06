package com.kh.codereview.post.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;

    @Size(max = 500, message = "요약은 500자 이하로 입력해주세요.")
    private String summary;

    @NotBlank(message = "코드는 필수입니다.")
    @Size(max = 10000, message = "코드는 10,000자를 초과할 수 없습니다.")
    private String codeContent;

    @NotBlank(message = "언어는 필수입니다.")
    @Size(max = 50)
    private String language;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(max = 50)
    private String category;

    @Size(max = 500, message = "리뷰 포커스는 500자 이하로 입력해주세요.")
    private String reviewFocus;
}