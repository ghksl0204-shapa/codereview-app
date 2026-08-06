package com.kh.codereview.post.model.service;

import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.post.model.dao.PostRepository;
import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.post.model.vo.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Post/Comment 서비스에서 공통으로 필요한 "활성 게시글 조회" 로직을 모아 중복을 제거한다.
 */
@Component
@RequiredArgsConstructor
public class PostFinder {

    private final PostRepository postRepository;

    public Post getActivePost(Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> BusinessException.notFound(
                        "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."));
    }

    // AI 리뷰 백그라운드 생성 시점에는 그 사이 게시글이 삭제됐을 수도 있으므로
    // 상태를 따지지 않고 조회하고, 없으면 null을 돌려줘 호출부가 조용히 스킵하게 한다.
    public Post findByIdIgnoringStatus(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
