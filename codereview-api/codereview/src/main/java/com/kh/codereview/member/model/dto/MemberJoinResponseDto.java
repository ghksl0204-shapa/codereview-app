package com.kh.codereview.member.model.dto;

import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.member.model.vo.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberJoinResponseDto {

    private String id;
    private String nickname;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static MemberJoinResponseDto from(Member member) {
        return MemberJoinResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}