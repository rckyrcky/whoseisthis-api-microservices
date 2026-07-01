package com.whoseisthis.gateway.report.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.whoseisthis.gateway.report.core.ReportStatus;
import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReportResponseDto(Long id, String title, String description, String location, ReportType type,
                                ReportStatus status, UserResponseDto reporter, OffsetDateTime createdAt) {
    public ReportResponseDto(Long id)
    {
        this(id, null, null, null, null, null, null, null);
    }
}
