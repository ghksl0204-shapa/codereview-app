package com.kh.codereview.comment.model.dao;

import com.kh.codereview.comment.model.vo.Comment;
import com.kh.codereview.comment.model.vo.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndStatus(Long id, CommentStatus status);
}