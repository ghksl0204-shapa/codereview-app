package com.kh.codereview.comment.controller;

import com.kh.codereview.comment.model.dto.CommentCreateRequestDto;
import com.kh.codereview.comment.model.dto.CommentListRequestDto;
import com.kh.codereview.comment.model.dto.CommentResponseDto;
import com.kh.codereview.comment.model.dto.CommentUpdateRequestDto;
import com.kh.codereview.comment.model.service.CommentService;
import com.kh.codereview.common.dto.PageResponseDto;
import com.kh.codereview.common.response.ApiResponse;
import com.kh.codereview.common.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentCreateRequestDto requestDto) {

        CommentResponseDto response = commentService.createComment(userDetails.getMemberId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 등록되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<CommentResponseDto>>> getComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Validated CommentListRequestDto condition) {

        PageResponseDto<CommentResponseDto> response =
                commentService.getComments(condition, userDetails.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공", response));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto requestDto) {

        CommentResponseDto response = commentService.updateComment(userDetails.getMemberId(), commentId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {

        commentService.deleteComment(userDetails.getMemberId(), commentId);

        return ResponseEntity.noContent().build();
    }
}
