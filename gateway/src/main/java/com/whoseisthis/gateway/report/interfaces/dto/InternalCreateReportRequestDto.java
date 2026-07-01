package com.whoseisthis.gateway.report.interfaces.dto;

import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;

public record InternalCreateReportRequestDto(String title, String description, String location, ReportType type,
                                             UserResponseDto reporter) {
}
