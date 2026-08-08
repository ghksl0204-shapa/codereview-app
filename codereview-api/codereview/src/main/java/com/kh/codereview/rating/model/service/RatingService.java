package com.kh.codereview.rating.model.service;

import com.kh.codereview.comment.model.dao.CommentRepository;
import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.comment.model.vo.CommentStatus;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.common.util.KstDateTime;
import com.kh.codereview.member.model.service.MemberFinder;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.rating.model.dao.RatingRepository;
import com.kh.codereview.rating.model.dto.RatingCreateRequestDto;
import com.kh.codereview.rating.model.dto.RatingResponseDto;
import com.kh.codereview.rating.model.vo.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
