package com.kh.codereview.common.security.userdetails;

import com.kh.codereview.member.model.dao.MemberRepository;
import com.kh.codereview.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 로그인 및 JWT 인증 모두 memberId(회원이 직접 만드는 아이디) 기준이므로
    // username 파라미터는 memberId로 취급한다.
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByIdAndWithdrawnFalse(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다: " + memberId));

        return new CustomUserDetails(member);
    }
}
