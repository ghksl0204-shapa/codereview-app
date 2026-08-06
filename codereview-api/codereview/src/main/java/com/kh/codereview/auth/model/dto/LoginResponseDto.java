package com.kh.codereview.auth.model.dto;

import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.member.model.vo.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String id;
    private String nickname;
    private Role role;

    public static LoginResponseDto of(Member member, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(member.getId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}