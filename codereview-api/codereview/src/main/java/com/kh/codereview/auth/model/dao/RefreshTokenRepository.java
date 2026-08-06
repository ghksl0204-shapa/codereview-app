package com.kh.codereview.auth.model.dao;

import com.kh.codereview.auth.model.vo.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenAndRevokedFalse(String refreshToken);

    void deleteAllByMemberId(String memberId);
}