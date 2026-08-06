package com.kh.codereview.auth.controller;

import com.kh.codereview.auth.model.dto.*;
import com.kh.codereview.auth.model.service.AuthService;
import com.kh.codereview.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequestDto requestDto) {
        authService.logout(requestDto);
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponseDto>> reissue(@Valid @RequestBody ReissueRequestDto requestDto) {
        ReissueResponseDto response = authService.reissue(requestDto);
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }
}