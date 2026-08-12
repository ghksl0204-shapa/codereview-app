package com.kh.codereview.post.model.service;

import com.kh.codereview.aireview.model.dao.AIReviewRepository;
import com.kh.codereview.aireview.model.service.AIReviewService;
import com.kh.codereview.aireview.model.vo.AIReview;
import com.kh.codereview.common.dto.PageResponseDto;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.member.model.service.MemberFinder;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.post.model.dao.PostRepository;
import com.kh.codereview.post.model.dto.*;
import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.post.model.vo.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberFinder memberFinder;
    private final PostFinder postFinder;
    private final AIReviewRepository aiReviewRepository;
    private final AIReviewService aiReviewService;

    @Transactional
    public PostResponseDto createPost(String memberId, PostCreateRequestDto requestDto) {
        Member member = memberFinder.getActiveMember(memberId);

        Post post = requestDto.toEntity(member);
        postRepository.save(post);

        AIReview aiReview = aiReviewService.createPendingReview(post);
        return PostResponseDto.of(post, aiReview);
    }

    public PageResponseDto<PostSummaryDto> getPosts(PostListRequestDto condition) {
        List<Post> posts = postRepository.searchPosts(
                PostStatus.ACTIVE, condition.getCategory(), condition.getLanguage(),
                condition.getKeyword(), condition.getOffset(), condition.getLimit());

        long totalCount = postRepository.countPosts(
                PostStatus.ACTIVE, condition.getCategory(), condition.getLanguage(), condition.getKeyword());

        List<PostSummaryDto> items = posts.stream()
                .map(post -> PostSummaryDto.of(post, getLatestAiReview(post.getId())))
                .toList();

        return PageResponseDto.of(items, condition.getOffset(), condition.getLimit(), totalCount);
    }

    public PostResponseDto getPost(Long postId) {
        Post post = postFinder.getActivePost(postId);
        return PostResponseDto.of(post, getLatestAiReview(postId));
    }

    @Transactional
    public PostResponseDto updatePost(String memberId, Long postId, PostUpdateRequestDto requestDto) {
        Post post = postFinder.getActivePost(postId);
        validateOwner(post, memberId);

        post.update(requestDto.getTitle(), requestDto.getSummary(), requestDto.getCodeContent(),
                requestDto.getLanguage(), requestDto.getCategory(), requestDto.getReviewFocus());

        return PostResponseDto.of(post, getLatestAiReview(postId));
    }

    @Transactional
    public void deletePost(String memberId, Long postId) {
        Post post = postFinder.getActivePost(postId);
        validateOwner(post, memberId);
        post.delete();
    }

    private AIReview getLatestAiReview(Long postId) {
        return aiReviewRepository.findFirstByPostIdOrderByCreatedAtDesc(postId).orElse(null);
    }

    private void validateOwner(Post post, String memberId) {
        if (!post.isOwnedBy(memberId)) {
            throw BusinessException.forbidden("POST_FORBIDDEN", "게시글에 대한 권한이 없습니다.");
        }
    }
}
