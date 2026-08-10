package com.kh.codereview.rating.model.dao;

import com.kh.codereview.rating.model.vo.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {

    boolean existsByCommentIdAndMemberId(Long commentId, String memberId);

    Optional<Rating> findByCommentIdAndMemberId(Long commentId, String memberId);
}