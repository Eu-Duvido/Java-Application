package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.pagination.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static <D, R> PageResponse<R> fromDomain(PageResult<D> page, Function<D, R> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.totalElements(),
                page.totalPages(),
                page.currentPage(),
                page.pageSize()
        );
    }
}