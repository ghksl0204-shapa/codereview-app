package com.kh.codereview.post.model.dao;

import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.post.model.vo.PostStatus;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> searchPosts(PostStatus status, String category, String language,
                            String keyword, int offset, int limit);

    long countPosts(PostStatus status, String category, String language, String keyword);
}