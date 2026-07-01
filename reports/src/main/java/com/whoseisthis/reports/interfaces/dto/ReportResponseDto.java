package com.whoseisthis.reports.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReportResponseDto(Long id, String title, String description, String location, ReportType type,
                                ReportStatus status, ReporterResponseDto reporter, OffsetDateTime createdAt) {
    public ReportResponseDto(Long id) {
        this(id, null, null, null, null, null, null, null);
    }
}
