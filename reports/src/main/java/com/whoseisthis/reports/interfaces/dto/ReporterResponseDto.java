package com.whoseisthis.reports.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReporterResponseDto(Long id, String name, String email) {
    public ReporterResponseDto(Long id) {
        this(id, null, null);
    }
}
