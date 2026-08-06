package com.kh.codereview.post.model.dao;

import com.kh.codereview.post.model.vo.Post;
import com.kh.codereview.post.model.vo.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Post> searchPosts(PostStatus status, String category, String language,
                                   String keyword, int offset, int limit) {
        StringBuilder jpql = new StringBuilder(
                "select p from Post p where p.status = :status ");
        Map<String, Object> params = buildFilterParams(status, category, language, keyword, jpql);
        jpql.append("order by p.createdAt desc");

        TypedQuery<Post> query = em.createQuery(jpql.toString(), Post.class);
        params.forEach(query::setParameter);

        return query.setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long countPosts(PostStatus status, String category, String language, String keyword) {
        StringBuilder jpql = new StringBuilder(
                "select count(p) from Post p where p.status = :status ");
        Map<String, Object> params = buildFilterParams(status, category, language, keyword, jpql);

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        params.forEach(query::setParameter);

        return query.getSingleResult();
    }

    private Map<String, Object> buildFilterParams(PostStatus status, String category, String language,
                                                    String keyword, StringBuilder jpql) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);

        if (category != null && !category.isBlank()) {
            jpql.append("and p.category = :category ");
            params.put("category", category);
        }
        if (language != null && !language.isBlank()) {
            jpql.append("and p.language = :language ");
            params.put("language", language);
        }
        if (keyword != null && !keyword.isBlank()) {
            jpql.append("and (p.title like :keyword or p.summary like :keyword) ");
            params.put("keyword", "%" + keyword + "%");
        }
        return params;
    }
}