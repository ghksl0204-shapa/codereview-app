package com.kh.codereview.comment.model.dao;

import com.kh.codereview.comment.model.vo.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Comment> searchComments(Long postId, int offset, int limit) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c " +
                        "where c.post.id = :postId " +
                        "order by coalesce(c.parentComment.id, c.id) asc, " +
                        "case when c.parentComment is null then 0 else 1 end asc, " +
                        "c.createdAt asc",
                Comment.class);
        query.setParameter("postId", postId);

        return query.setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long countComments(Long postId) {
        TypedQuery<Long> query = em.createQuery(
                "select count(c) from Comment c where c.post.id = :postId",
                Long.class);
        query.setParameter("postId", postId);
        return query.getSingleResult();
    }
}