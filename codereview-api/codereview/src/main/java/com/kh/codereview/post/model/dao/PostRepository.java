package com.kh.codereview.post.model.dao;

import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.post.model.vo.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndStatus(Long id, PostStatus status);
}