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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIReviewService {

    // 실제 게시글 데이터(27건)를 직접 확인해 정한 값 — 14자 log.info("밥"); 는 통과,
    // 그 아래 8건(낙서/무의미 텍스트)은 차단되는 경계가 10자였음.
    private static final int MIN_CODE_LENGTH = 10;
    // ASCII(코드 포인트 0~127, 개행·탭 포함) 문자 비율. 실제 DB의 경계 사례로 검증:
    // log.info("밥"); 92.9% 통과 / 이모지 스팸 0% 차단 / 조지아 문자 텍스트 20.7% 차단.
    // 영어 산문은 ASCII 비율이 높아 이 지표로 못 거른다 — 알려진 사각지대이며, 거부 로그로 사후 확인한다.
    private static final double MIN_ASCII_RATIO = 0.30;

    private final AIReviewRepository aiReviewRepository;
    private final PostFinder postFinder;
    private final ClaudeReviewClient claudeReviewClient;
    private final ApplicationEventPublisher eventPublisher;

    // 게시글 저장 트랜잭션 안에서 호출된다: PENDING row 저장까지만 같이 커밋되고,
    // 실제 AI 호출은 커밋 이후 이벤트 리스너(handleAIReviewRequested)에서 비동기로 진행된다.
    // 프론트는 이 사이 GET /api/posts/{postId}를 폴링해서 aiReviewStatus 변화를 확인한다.
    @Transactional
    public AIReview createPendingReview(Post post) {
        validateContentEligible(post);

        AIReview aiReview = AIReview.builder()
                .postId(post.getId())
                .codeHash(hashOf(post.getCodeContent()))
                .build();
        AIReview saved = aiReviewRepository.save(aiReview);

        eventPublisher.publishEvent(new AIReviewRequestedEvent(saved.getId(), post.getId()));
        return saved;
    }

    @Transactional
    public AIReviewResponseDto regenerate(String memberId, Long postId) {
        Post post = postFinder.getActivePost(postId);
        validateOwner(post, memberId);
        validateNotAlreadyInProgress(postId);

        Optional<AIReview> reusable = findReusableReview(post);
        if (reusable.isPresent()) {
            return AIReviewResponseDto.of(reusable.get());
        }

        AIReview aiReview = createPendingReview(post);
        return AIReviewResponseDto.of(aiReview);
    }

    // 코드 본문이 마지막으로 완료된 리뷰 이후 바뀌지 않았다면, AI를 다시 호출하지 않고
    // 기존 완료 리뷰를 그대로 재사용한다(비용 방어 조치 3).
    private Optional<AIReview> findReusableReview(Post post) {
        String currentHash = hashOf(post.getCodeContent());
        return aiReviewRepository.findFirstByPostIdOrderByCreatedAtDesc(post.getId())
                .filter(review -> review.getStatus() == AIReviewStatus.COMPLETED)
                .filter(review -> currentHash.equals(review.getCodeHash()));
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

    private void validateContentEligible(Post post) {
        String content = post.getCodeContent();
        if (content.length() < MIN_CODE_LENGTH) {
            log.warn("AI 리뷰 거부 - 길이 미달: postId={}, length={}", post.getId(), content.length());
            throw BusinessException.badRequest(
                    "CODE_TOO_SHORT", "코드가 너무 짧아 AI 리뷰를 생성할 수 없습니다. 코드 내용을 입력해주세요.");
        }

        double asciiRatio = asciiRatio(content);
        if (asciiRatio < MIN_ASCII_RATIO) {
            log.warn("AI 리뷰 거부 - 코드로 보기 어려움: postId={}, length={}, asciiRatio={}",
                    post.getId(), content.length(), asciiRatio);
            throw BusinessException.badRequest(
                    "NOT_CODE_LIKE", "코드가 아닌 일반 텍스트로 보입니다. 코드를 입력했는지 확인해주세요.");
        }
    }

    private double asciiRatio(String content) {
        long asciiCount = content.chars().filter(c -> c < 128).count();
        return (double) asciiCount / content.length();
    }

    private String hashOf(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}