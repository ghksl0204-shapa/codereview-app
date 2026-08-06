package com.kh.codereview.member.model.dao;

import com.kh.codereview.member.model.vo.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsById(String id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, String id);

    Optional<Member> findByIdAndWithdrawnFalse(String id);
}