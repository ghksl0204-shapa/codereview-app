package com.kh.codereview.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponseDto<T> {

    private List<T> items;
    private int offset;
    private int limit;
    private long totalCount;
    private boolean hasNext;

    public static <T> PageResponseDto<T> of(List<T> items, int offset, int limit, long totalCount) {
        return PageResponseDto.<T>builder()
                .items(items)
                .offset(offset)
                .limit(limit)
                .totalCount(totalCount)
                .hasNext(offset + items.size() < totalCount)
                .build();
    }
}