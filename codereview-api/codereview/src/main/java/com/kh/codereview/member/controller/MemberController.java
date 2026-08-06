package com.kh.codereview.member.controller;

import com.kh.codereview.common.response.ApiResponse;
import com.kh.codereview.common.security.userdetails.CustomUserDetails;
import com.kh.codereview.member.model.dto.*;
import com.kh.codereview.member.model.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberJoinResponseDto>> join(
            @Valid @RequestBody MemberJoinRequestDto requestDto) {
        MemberJoinResponseDto response = memberService.join(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", response));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<MemberJoinResponseDto>> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateNicknameRequestDto requestDto) {
        MemberJoinResponseDto response = memberService.updateNickname(userDetails.getMemberId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("닉네임이 변경되었습니다.", response));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdatePasswordRequestDto requestDto) {
        memberService.updatePassword(userDetails.getMemberId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("비밀번호가 변경되었습니다.", null));
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberWithdrawRequestDto requestDto) {
        memberService.withdraw(userDetails.getMemberId(), requestDto);
        return ResponseEntity.noContent().build();
    }
}
