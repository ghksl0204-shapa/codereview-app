package com.kh.codereview.member.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdatePasswordRequestDto {

    // 새 규칙을 걸지 않는다 — 기존 계정의 비밀번호가 새 규칙을 위반해도
    // 현재 비밀번호로 인증해 규칙에 맞는 값으로 변경할 수 있어야 한다
    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
    private String currentPassword;

    // 규칙은 MemberJoinRequestDto.password와 반드시 동일하게 유지할 것
    // 필수: 영문 1자 이상 + 숫자 1자 이상 / 차단: 공백 / 그 외 특수문자는 제한하지 않음
    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "새 비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])\\S+$",
            message = "새 비밀번호는 영문과 숫자를 각각 1자 이상 포함하고, 공백 없이 입력해주세요.")
    private String newPassword;
}