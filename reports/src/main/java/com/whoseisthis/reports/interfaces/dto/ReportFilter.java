package com.whoseisthis.reports.interfaces.dto;

import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReportFilter(
        @Size(max = 20, message = "Query parameter must not exceed 20 characters")
        String title,
        @Size(max = 20, message = "Query parameter must not exceed 20 characters")
        String location,
        @Size(max = 20, message = "Query parameter must not exceed 20 characters")
        String reporter,
        ReportStatus status,
        ReportType type,
        @PastOrPresent(message = "Invalid date")
        LocalDate from,
        @PastOrPresent(message = "Invalid date")
        LocalDate to) {
}
