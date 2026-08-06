package com.kh.codereview.member.model.service;

import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.member.model.dao.MemberRepository;
import com.kh.codereview.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 여러 도메인 서비스(Post, Comment, Rating 등)에서 공통으로 필요한
 * "활성 회원 조회" 로직을 한 곳에 모아 중복을 제거한다.
 * 로그인(AuthService)처럼 자격 증명 실패 메시지를 별도로 다뤄야 하는 경우는
 * 이 컴포넌트를 사용하지 않는다.
 */
@Component
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberRepository memberRepository;

    public Member getActiveMember(String memberId) {
        return memberRepository.findByIdAndWithdrawnFalse(memberId)
                .orElseThrow(() -> BusinessException.notFound(
                        "MEMBER_NOT_FOUND", "회원 정보를 찾을 수 없습니다."));
    }
}
