package com.kh.codereview.member.model.service;

import com.kh.codereview.auth.model.dao.RefreshTokenRepository;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.member.model.dao.MemberRepository;
import com.kh.codereview.member.model.dto.*;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.member.model.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberFinder memberFinder;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberJoinResponseDto join(MemberJoinRequestDto requestDto) {
        validateDuplicate(requestDto);

        Member member = Member.builder()
                .id(requestDto.getId())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .role(Role.USER)
                .build();

        Member saved = memberRepository.save(member);
        return MemberJoinResponseDto.from(saved);
    }

    @Transactional
    public MemberJoinResponseDto updateNickname(String memberId, MemberUpdateNicknameRequestDto requestDto) {
        Member member = memberFinder.getActiveMember(memberId);
        validateNicknameDuplicate(requestDto.getNickname(), memberId);
        member.updateNickname(requestDto.getNickname());
        return MemberJoinResponseDto.from(member);
    }

    @Transactional
    public void updatePassword(String memberId, MemberUpdatePasswordRequestDto requestDto) {
        Member member = memberFinder.getActiveMember(memberId);
        validateCurrentPassword(requestDto.getCurrentPassword(), member.getPassword());
        member.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    @Transactional
    public void withdraw(String memberId, MemberWithdrawRequestDto requestDto) {
        Member member = memberFinder.getActiveMember(memberId);
        validateCurrentPassword(requestDto.getCurrentPassword(), member.getPassword());
        member.withdraw();
        refreshTokenRepository.deleteAllByMemberId(memberId);
    }

    private void validateDuplicate(MemberJoinRequestDto requestDto) {
        if (memberRepository.existsById(requestDto.getId())) {
            throw BusinessException.conflict("DUPLICATE_ID", "이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw BusinessException.conflict("DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다.");
        }
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw BusinessException.conflict("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.");
        }
    }

    private void validateNicknameDuplicate(String nickname, String currentMemberId) {
        if (memberRepository.existsByNicknameAndIdNot(nickname, currentMemberId)) {
            throw BusinessException.conflict("DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다.");
        }
    }

    private void validateCurrentPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw BusinessException.unauthorized(
                    "INVALID_PASSWORD", "현재 비밀번호가 일치하지 않습니다.");
        }
    }
}
