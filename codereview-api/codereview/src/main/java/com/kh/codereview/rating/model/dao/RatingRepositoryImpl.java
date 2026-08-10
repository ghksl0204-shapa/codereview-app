package com.kh.codereview.rating.model.dao;

import com.kh.codereview.rating.model.dto.RatingAggregateDto;
import com.kh.codereview.rating.model.vo.Rating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<RatingAggregateDto> aggregateByCommentIds(List<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return List.of();
        }

        TypedQuery<RatingAggregateDto> query = em.createQuery(
                "select new com.kh.codereview.rating.model.dto.RatingAggregateDto(" +
                        "r.comment.id, avg(r.kindnessScore), avg(r.accuracyScore), avg(r.detailScore), count(r)) " +
                        "from Rating r where r.comment.id in :commentIds group by r.comment.id",
                RatingAggregateDto.class);
        query.setParameter("commentIds", commentIds);

        return query.getResultList();
    }

    @Override
    public List<Rating> findMyRatingsByCommentIds(List<Long> commentIds, String memberId) {
        if (commentIds.isEmpty()) {
            return List.of();
        }

        TypedQuery<Rating> query = em.createQuery(
                "select r from Rating r join fetch r.member " +
                        "where r.comment.id in :commentIds and r.member.id = :memberId",
                Rating.class);
        query.setParameter("commentIds", commentIds);
        query.setParameter("memberId", memberId);

        return query.getResultList();
    }
}
