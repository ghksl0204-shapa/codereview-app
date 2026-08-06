package com.kh.codereview.aireview.event;

import lombok.Getter;

/**
 * AIReview row가 PENDING으로 저장 커밋된 후, 실제 AI 호출을 트리거하기 위한 이벤트.
 * 이벤트로 분리한 이유: @Async 메서드를 같은 클래스 안에서 직접 호출하면 프록시를 안 거쳐서
 * 비동기 처리가 안 먹는 self-invocation 문제가 생기는데, 이벤트 발행/구독 방식은 그 문제를 피할 수 있다.
 */
@Getter
public class AIReviewRequestedEvent {

    private final Long aiReviewId;
    private final Long postId;

    public AIReviewRequestedEvent(Long aiReviewId, Long postId) {
        this.aiReviewId = aiReviewId;
        this.postId = postId;
    }
}
