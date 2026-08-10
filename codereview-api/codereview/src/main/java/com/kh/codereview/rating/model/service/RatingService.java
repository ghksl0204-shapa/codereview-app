package com.kh.codereview.rating.model.service;

import com.kh.codereview.comment.model.dao.CommentRepository;
import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.comment.model.vo.CommentStatus;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.common.util.KstDateTime;
import com.kh.codereview.member.model.service.MemberFinder;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.rating.model.dao.RatingRepository;
import com.kh.codereview.rating.model.dto.RatingAggregateDto;
import com.kh.codereview.rating.model.dto.RatingCreateRequestDto;
import com.kh.codereview.rating.model.dto.RatingResponseDto;
import com.kh.codereview.rating.model.dto.RatingSummaryDto;
import com.kh.codereview.rating.model.dto.RatingUpdateRequestDto;
import com.kh.codereview.rating.model.vo.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final MemberFinder memberFinder;

    @Transactional
    public RatingResponseDto createRating(Long commentId, String memberId, RatingCreateRequestDto requestDto) {
        Comment comment = getNormalCommentOrThrow(commentId);
        Member member = memberFinder.getActiveMember(memberId);

        validateNotDuplicateRating(commentId, memberId);

        Rating rating = Rating.builder()
                .comment(comment)
                .member(member)
                .kindnessScore(requestDto.getKindnessScore())
                .accuracyScore(requestDto.getAccuracyScore())
                .detailScore(requestDto.getDetailScore())
                .commentText(requestDto.getCommentText())
                .build();

        Rating saved = ratingRepository.save(rating);

        return toResponseDto(saved);
    }

    @Transactional
    public RatingResponseDto updateRating(Long commentId, String memberId, RatingUpdateRequestDto requestDto) {
        getNormalCommentOrThrow(commentId);

        Rating rating = ratingRepository.findByCommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> BusinessException.notFound(
                        "RATING_NOT_FOUND", "등록된 평점이 없습니다. 먼저 평점을 등록해주세요."));

        rating.update(requestDto.getKindnessScore(), requestDto.getAccuracyScore(),
                requestDto.getDetailScore(), requestDto.getCommentText());

        return toResponseDto(rating);
    }

    public Map<Long, RatingSummaryDto> getSummariesByCommentIds(List<Long> commentIds, String memberId) {
        if (commentIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, RatingAggregateDto> aggregates = ratingRepository.aggregateByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(RatingAggregateDto::getCommentId, a -> a));
        Map<Long, Rating> myRatings = ratingRepository.findMyRatingsByCommentIds(commentIds, memberId).stream()
                .collect(Collectors.toMap(r -> r.getComment().getId(), r -> r));

        Map<Long, RatingSummaryDto> result = new HashMap<>();
        for (Long commentId : commentIds) {
            RatingAggregateDto aggregate = aggregates.get(commentId);
            Rating myRating = myRatings.get(commentId);

            if (aggregate == null) {
                result.put(commentId, RatingSummaryDto.builder()
                        .ratingCount(0)
                        .averageKindnessScore(null)
                        .averageAccuracyScore(null)
                        .averageDetailScore(null)
                        .myRating(myRating != null ? toResponseDto(myRating) : null)
                        .build());
                continue;
            }

            result.put(commentId, RatingSummaryDto.builder()
                    .ratingCount(aggregate.getRatingCount())
                    .averageKindnessScore(round(aggregate.getAvgKindnessScore()))
                    .averageAccuracyScore(round(aggregate.getAvgAccuracyScore()))
                    .averageDetailScore(round(aggregate.getAvgDetailScore()))
                    .myRating(myRating != null ? toResponseDto(myRating) : null)
                    .build());
        }
        return result;
    }

    private static Double round(Double value) {
        return value == null ? null : Math.round(value * 10) / 10.0;
    }

    private Comment getNormalCommentOrThrow(Long commentId) {
        return commentRepository.findByIdAndStatus(commentId, CommentStatus.NORMAL)
                .orElseThrow(() -> BusinessException.notFound(
                        "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."));
    }

    private void validateNotDuplicateRating(Long commentId, String memberId) {
        if (ratingRepository.existsByCommentIdAndMemberId(commentId, memberId)) {
            throw BusinessException.badRequest(
                    "RATING_ALREADY_EXISTS", "이미 해당 댓글에 별점을 부여했습니다.");
        }
    }

    private RatingResponseDto toResponseDto(Rating rating) {
        return RatingResponseDto.builder()
                .id(rating.getId())
                .commentId(rating.getComment().getId())
                .memberId(rating.getMember().getId())
                .memberNickname(rating.getMember().getNickname())
                .kindnessScore(rating.getKindnessScore())
                .accuracyScore(rating.getAccuracyScore())
                .detailScore(rating.getDetailScore())
                .commentText(rating.getCommentText())
                .createdAt(KstDateTime.from(rating.getCreatedAt()))
                .build();
    }
}
