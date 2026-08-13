package com.kh.codereview.member.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateNicknameRequestDto {

    // 규칙은 MemberJoinRequestDto.nickname과 반드시 동일하게 유지할 것
    // 차단: 공백만 / 문자 종류는 제한하지 않는다 (한글 닉네임 허용)
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^\\S*$", message = "닉네임에는 공백을 사용할 수 없습니다.")
    private String nickname;
}