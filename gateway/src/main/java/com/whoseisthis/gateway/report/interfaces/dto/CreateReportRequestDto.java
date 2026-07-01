package com.whoseisthis.gateway.report.interfaces.dto;

import com.whoseisthis.gateway.report.core.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequestDto(
        @NotBlank(message = "Title can't be empty")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,
        @NotBlank(message = "Description can't be empty")
        @Size(max = 200, message = "Description must not exceed 200 characters")
        String description,
        @NotBlank(message = "Location can't be empty")
        @Size(max = 200, message = "Location must not exceed 200 characters")
        String location,
        @NotNull(message = "Type can't be empty")
        ReportType type
) {
}
