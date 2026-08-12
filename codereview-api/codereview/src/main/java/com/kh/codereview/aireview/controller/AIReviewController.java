package com.kh.codereview.aireview.controller;

import com.kh.codereview.aireview.model.dto.AIReviewResponseDto;
import com.kh.codereview.aireview.model.service.AIReviewService;
import com.kh.codereview.common.response.ApiResponse;
import com.kh.codereview.common.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// AI 리뷰 진행 상황은 별도 스트림 없이, 프론트가 GET /api/posts/{postId}를
// 폴링해서 aiReviewStatus 필드로 확인한다. 이 컨트롤러는 재생성 요청만 담당한다.
@RestController
@RequestMapping("/api/posts/{postId}/ai-review")
@RequiredArgsConstructor
public class AIReviewController {

    private final AIReviewService aiReviewService;

    @PostMapping("/regenerate")
    public ResponseEntity<ApiResponse<AIReviewResponseDto>> regenerate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        AIReviewResponseDto response = aiReviewService.regenerate(userDetails.getMemberId(), postId);

        // 코드가 바뀌지 않아 새로 호출하지 않고 기존 완료된 리뷰를 그대로 반환한 경우,
        // regenerate()는 이미 COMPLETED 상태인 리뷰를 즉시 돌려준다(신규 생성 시엔 PENDING).
        boolean reused = "COMPLETED".equals(response.getStatus());
        HttpStatus status = reused ? HttpStatus.OK : HttpStatus.CREATED;
        String message = reused
                ? "코드가 변경되지 않아 기존 리뷰 결과를 반환합니다."
                : "AI 리뷰 재생성을 요청했습니다.";

        return ResponseEntity.status(status).body(ApiResponse.success(message, response));
    }
}
