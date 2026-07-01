package com.whoseisthis.reports.interfaces.dto;

import com.whoseisthis.reports.core.ReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReportRequestDto(
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,
        @Size(max = 200, message = "Description must not exceed 200 characters")
        String description,
        @Size(max = 200, message = "Location must not exceed 200 characters")
        String location,
        @NotNull(message = "Type can't be empty")
        ReportType type
) {
}
