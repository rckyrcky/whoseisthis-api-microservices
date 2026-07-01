package com.whoseisthis.gateway.interfaces.dto.response;

import java.util.List;

public record PaginationResponse<T>(
        List<T> data,
        int page,
        int limit,
        long totalElements,
        int totalPages
) {
}
