package com.kh.codereview.member.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberWithdrawRequestDto {

    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
    private String currentPassword;
}