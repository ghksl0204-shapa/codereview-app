package com.kh.codereview.comment.model.dto;

import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.member.model.vo.Member;
import com.kh.codereview.post.model.vo.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequestDto {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 1000, message = "댓글은 1000자 이하로 입력해주세요.")
    private String content;

    private Long parentCommentId;

    public Comment toEntity(Post post, Member member, Comment parentComment) {
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .parentComment(parentComment)
                .build();
    }
}