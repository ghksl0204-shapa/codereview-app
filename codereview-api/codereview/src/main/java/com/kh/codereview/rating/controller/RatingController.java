package com.kh.codereview.rating.controller;

import com.kh.codereview.common.response.ApiResponse;
import com.kh.codereview.common.security.userdetails.CustomUserDetails;
import com.kh.codereview.rating.model.dto.RatingCreateRequestDto;
import com.kh.codereview.rating.model.dto.RatingResponseDto;
import com.kh.codereview.rating.model.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{commentId}/rating")
    public ResponseEntity<ApiResponse<RatingResponseDto>> createRating(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RatingCreateRequestDto requestDto) {

        RatingResponseDto result = ratingService.createRating(commentId, userDetails.getMemberId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("별점이 등록되었습니다.", result));
    }
}
