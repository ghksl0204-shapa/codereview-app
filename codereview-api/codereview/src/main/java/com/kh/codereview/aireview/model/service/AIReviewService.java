package com.kh.codereview.aireview.model.service;

import com.kh.codereview.aireview.event.AIReviewRequestedEvent;
import com.kh.codereview.aireview.model.dao.AIReviewRepository;
import com.kh.codereview.aireview.model.dto.AIReviewResponseDto;
import com.kh.codereview.aireview.model.vo.AIReview;
import com.kh.codereview.aireview.model.vo.AIReviewStatus;
import com.kh.codereview.common.exception.BusinessException;
import com.kh.codereview.post.model.service.PostFinder;
import com.kh.codereview.post.model.vo.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIReviewService {

    private final AIReviewRepository aiReviewRepository;
    private final PostFinder postFinder;
    private final ClaudeReviewClient claudeReviewClient;
    private final ApplicationEventPublisher eventPublisher;

    // 게시글 저장 트랜잭션 안에서 호출된다: PENDING row 저장까지만 같이 커밋되고,
    // 실제 AI 호출은 커밋 이후 이벤트 리스너(handleAIReviewRequested)에서 비동기로 진행된다.
    // 프론트는 이 사이 GET /api/posts/{postId}를 폴링해서 aiReviewStatus 변화를 확인한다.
    @Transactional
    public AIReview createPendingReview(Long postId) {
        AIReview aiReview = AIReview.builder()
                .postId(postId)
                .build();
        AIReview saved = aiReviewRepository.save(aiReview);

        eventPublisher.publishEvent(new AIReviewRequestedEvent(saved.getId(), postId));
        return saved;
    }

    @Transactional
    public AIReviewResponseDto regenerate(String memberId, Long postId) {
        Post post = postFinder.getActivePost(postId);
        validateOwner(post, memberId);
        validateNotAlreadyInProgress(postId);

        AIReview aiReview = createPendingReview(postId);
        return AIReviewResponseDto.of(aiReview);
    }

    @Async("aiReviewExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAIReviewRequested(AIReviewRequestedEvent event) {
        generateReview(event.getAiReviewId(), event.getPostId());
    }

    private void generateReview(Long aiReviewId, Long postId) {
        Post post = postFinder.findByIdIgnoringStatus(postId);
        if (post == null) {
            log.warn("AI 리뷰 생성 대상 게시글이 존재하지 않습니다. postId={}", postId);
            return;
        }

        try {
            String content = claudeReviewClient.requestReview(post);
            markCompleted(aiReviewId, content, claudeReviewClient.getModel());
        } catch (Exception e) {
            log.error("AI 리뷰 생성 실패: postId={}", postId, e);
            markFailed(aiReviewId);
        }
    }

    // 주의: 이 메서드는 handleAIReviewRequested -> generateReview -> markCompleted 처럼
    // 같은 클래스 내부에서 this.markCompleted(...) 형태로 호출된다(self-invocation).
    // 이 경우 Spring AOP 프록시를 거치지 않아 클래스에 붙은 @Transactional이 무시되고,
    // findById로 조회한 엔티티가 곧바로 detached 상태가 되어 complete()로 값만 바꿔도
    // DB에는 반영되지 않는다. 그래서 트랜잭션 유무와 무관하게 항상 반영되도록
    // aiReviewRepository.save(...)를 명시적으로 호출한다(detached 엔티티는 merge된다).
    @Transactional
    public void markCompleted(Long aiReviewId, String content, String model) {
        AIReview aiReview = aiReviewRepository.findById(aiReviewId)
                .orElseThrow(() -> new IllegalStateException("AIReview를 찾을 수 없습니다: " + aiReviewId));
        aiReview.complete(content, model);
        aiReviewRepository.save(aiReview);
    }

    @Transactional
    public void markFailed(Long aiReviewId) {
        AIReview aiReview = aiReviewRepository.findById(aiReviewId)
                .orElseThrow(() -> new IllegalStateException("AIReview를 찾을 수 없습니다: " + aiReviewId));
        aiReview.fail();
        aiReviewRepository.save(aiReview);
    }

    private void validateOwner(Post post, String memberId) {
        if (!post.isOwnedBy(memberId)) {
            throw BusinessException.forbidden("POST_FORBIDDEN", "게시글에 대한 권한이 없습니다.");
        }
    }

    private void validateNotAlreadyInProgress(Long postId) {
        aiReviewRepository.findFirstByPostIdOrderByCreatedAtDesc(postId)
                .filter(review -> review.getStatus() == AIReviewStatus.PENDING)
                .ifPresent(review -> {
                    throw BusinessException.badRequest(
                            "AI_REVIEW_IN_PROGRESS", "이미 AI 리뷰를 생성하고 있습니다. 완료 후 다시 시도해주세요.");
                });
    }
}