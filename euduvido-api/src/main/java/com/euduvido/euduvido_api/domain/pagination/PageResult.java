package com.euduvido.euduvido_api.domain.pagination;

import java.util.List;

/** Resultado paginado agnóstico de framework — usado em repositórios de domínio e use cases. */
public record PageResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(List.of(), 0L, 0, page, size);
    }
}
