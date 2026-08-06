package com.kh.codereview.rating.model.dao;

import com.kh.codereview.rating.model.vo.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByCommentIdAndMemberId(Long commentId, String memberId);
}