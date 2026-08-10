package com.kh.codereview.comment.model.service;

import com.kh.codereview.comment.model.dao.CommentRepository;
import com.kh.codereview.comment.model.dto.CommentCreateRequestDto;
import com.kh.codereview.comment.model.dto.CommentListRequestDto;
import com.kh.codereview.comment.model.dto.CommentResponseDto;
import com.kh.codereview.comment.model.dto.CommentUpdateRequestDto;
import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.comment.model.vo.CommentStatus;
import com.kh.codereview.common.dto.PageResponseDto;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.member.model.service.MemberFinder;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.post.model.service.PostFinder;
import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.rating.model.dto.RatingSummaryDto;
import com.kh.codereview.rating.model.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostFinder postFinder;
    private final MemberFinder memberFinder;
    private final RatingService ratingService;

    @Transactional
    public CommentResponseDto createComment(String memberId, CommentCreateRequestDto requestDto) {
        Member member = memberFinder.getActiveMember(memberId);
        Post post = postFinder.getActivePost(requestDto.getPostId());
        Comment parentComment = resolveParentComment(requestDto.getParentCommentId());

        Comment comment = requestDto.toEntity(post, member, parentComment);
        commentRepository.save(comment);

        return CommentResponseDto.of(comment);
    }

    public PageResponseDto<CommentResponseDto> getComments(CommentListRequestDto condition, String memberId) {
        postFinder.getActivePost(condition.getPostId());

        List<Comment> comments = commentRepository.searchComments(
                condition.getPostId(), condition.getOffset(), condition.getLimit());
        long totalCount = commentRepository.countComments(condition.getPostId());

        List<Long> commentIds = comments.stream().map(Comment::getId).toList();
        Map<Long, RatingSummaryDto> ratingSummaries = ratingService.getSummariesByCommentIds(commentIds, memberId);

        List<CommentResponseDto> items = comments.stream()
                .map(comment -> CommentResponseDto.of(
                        comment, ratingSummaries.getOrDefault(comment.getId(), RatingSummaryDto.empty())))
                .toList();

        return PageResponseDto.of(items, condition.getOffset(), condition.getLimit(), totalCount);
    }

    @Transactional
    public CommentResponseDto updateComment(String memberId, Long commentId, CommentUpdateRequestDto requestDto) {
        Comment comment = getNormalCommentOrThrow(commentId);
        validateOwner(comment, memberId);

        comment.updateContent(requestDto.getContent());
        return CommentResponseDto.of(comment);
    }

    @Transactional
    public void deleteComment(String memberId, Long commentId) {
        Comment comment = getNormalCommentOrThrow(commentId);
        validateOwner(comment, memberId);
        comment.delete();
    }

    private Comment resolveParentComment(Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }
        Comment parentComment = getNormalCommentOrThrow(parentCommentId);
        validateSingleDepth(parentComment);
        return parentComment;
    }

    private void validateSingleDepth(Comment parentComment) {
        if (parentComment.isReply()) {
            throw BusinessException.badRequest(
                    "COMMENT_DEPTH_EXCEEDED", "대댓글에는 답글을 작성할 수 없습니다.");
        }
    }

    private Comment getNormalCommentOrThrow(Long commentId) {
        return commentRepository.findByIdAndStatus(commentId, CommentStatus.NORMAL)
                .orElseThrow(() -> BusinessException.notFound(
                        "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."));
    }

    private void validateOwner(Comment comment, String memberId) {
        if (!comment.isOwnedBy(memberId)) {
            throw BusinessException.forbidden(
                    "COMMENT_FORBIDDEN", "댓글에 대한 권한이 없습니다.");
        }
    }
}
