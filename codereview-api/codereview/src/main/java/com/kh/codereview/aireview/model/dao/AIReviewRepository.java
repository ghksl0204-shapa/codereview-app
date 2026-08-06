package com.kh.codereview.aireview.model.dao;

import com.kh.codereview.aireview.model.vo.AIReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIReviewRepository extends JpaRepository<AIReview, Long> {

    Optional<AIReview> findFirstByPostIdOrderByCreatedAtDesc(Long postId);
}