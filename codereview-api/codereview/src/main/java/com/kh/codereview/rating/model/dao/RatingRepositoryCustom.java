package com.kh.codereview.rating.model.dao;

import com.kh.codereview.rating.model.dto.RatingAggregateDto;
import com.kh.codereview.rating.model.vo.Rating;

import java.util.List;

public interface RatingRepositoryCustom {

    List<RatingAggregateDto> aggregateByCommentIds(List<Long> commentIds);

    List<Rating> findMyRatingsByCommentIds(List<Long> commentIds, String memberId);
}
