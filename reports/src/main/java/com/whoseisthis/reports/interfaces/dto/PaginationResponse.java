package com.whoseisthis.reports.interfaces.dto;

import java.util.List;

public record PaginationResponse<T>(
        List<T> data,
        int page,
        int limit,
        long totalElements,
        int totalPages
) {
}
