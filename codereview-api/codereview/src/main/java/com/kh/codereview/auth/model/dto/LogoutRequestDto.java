package com.kh.codereview.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequestDto {

    @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
    private String refreshToken;
}