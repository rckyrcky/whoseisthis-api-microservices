package com.whoseisthis.users.interfaces.utils;

import com.whoseisthis.users.interfaces.dto.PaginationResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PaginationMapper {
    private PaginationMapper()
    {
    }

    public static <E, D> PaginationResponse<D> create(Page<E> page, Function<E, D> mapper)
    {
        return new PaginationResponse<D>(page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
