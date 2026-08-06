package com.kh.codereview.comment.model.dao;

import com.kh.codereview.comment.model.vo.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> searchComments(Long postId, int offset, int limit);

    long countComments(Long postId);
}