package com.kh.codereview.post.controller;

import com.kh.codereview.common.dto.PageResponseDto;
import com.kh.codereview.common.response.ApiResponse;
import com.kh.codereview.common.security.userdetails.CustomUserDetails;
import com.kh.codereview.post.model.dto.*;
import com.kh.codereview.post.model.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequestDto requestDto) {

        PostResponseDto response = postService.createPost(userDetails.getMemberId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 등록되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<PostSummaryDto>>> getPosts(
            @Validated PostListRequestDto condition) {

        PageResponseDto<PostSummaryDto> response = postService.getPosts(condition);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable Long postId) {
        PostResponseDto response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequestDto requestDto) {

        PostResponseDto response = postService.updatePost(userDetails.getMemberId(), postId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        postService.deletePost(userDetails.getMemberId(), postId);

        return ResponseEntity.noContent().build();
    }
}
