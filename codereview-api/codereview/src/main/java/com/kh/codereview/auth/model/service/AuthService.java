package com.kh.codereview.auth.model.service;

import com.kh.codereview.auth.model.dao.RefreshTokenRepository;
import com.kh.codereview.auth.model.dto.*;
import com.kh.codereview.auth.model.vo.RefreshToken;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.common.security.jwt.JwtTokenProvider;
import com.kh.codereview.member.model.dao.MemberRepository;
import com.kh.codereview.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        Member member = findMemberForLogin(requestDto.getId());
        validatePassword(requestDto.getPassword(), member.getPassword());

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole());
        String refreshToken = issueRefreshToken(member.getId());

        return LoginResponseDto.of(member, accessToken, refreshToken);
    }

    @Transactional
    public void logout(LogoutRequestDto requestDto) {
        RefreshToken token = findActiveRefreshToken(requestDto.getRefreshToken());
        token.revoke();
    }

    @Transactional
    public ReissueResponseDto reissue(ReissueRequestDto requestDto) {
        RefreshToken token = findActiveRefreshToken(requestDto.getRefreshToken());
        validateNotExpired(token);
        token.revoke();

        Member member = findMemberForLogin(token.getMemberId());
        String newAccessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole());
        String newRefreshToken = issueRefreshToken(member.getId());

        return ReissueResponseDto.of(newAccessToken, newRefreshToken);
    }

    // 로그인/토큰 재발급 시점의 회원 조회는 보안상 "아이디를 못 찾음"과 "비밀번호가 틀림"을
    // 구분하지 않고 동일한 메시지로 응답해야 하므로, 공용 MemberFinder(MEMBER_NOT_FOUND)를
    // 그대로 재사용하지 않고 이 서비스 안에서 별도로 처리한다.
    private Member findMemberForLogin(String id) {
        return memberRepository.findByIdAndWithdrawnFalse(id)
                .orElseThrow(() -> BusinessException.unauthorized(
                        "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw BusinessException.unauthorized(
                    "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    private RefreshToken findActiveRefreshToken(String refreshTokenValue) {
        return refreshTokenRepository.findByRefreshTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> BusinessException.unauthorized(
                        "INVALID_TOKEN", "유효하지 않은 토큰입니다."));
    }

    private void validateNotExpired(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw BusinessException.unauthorized("EXPIRED_TOKEN", "만료된 토큰입니다.");
        }
    }

    private String issueRefreshToken(String memberId) {
        String value = jwtTokenProvider.generateRefreshTokenValue();
        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberId)
                .refreshToken(value)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidityMs())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return value;
    }
}
