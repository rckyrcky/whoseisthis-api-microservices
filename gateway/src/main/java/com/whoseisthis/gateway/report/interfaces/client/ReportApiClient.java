package com.whoseisthis.gateway.report.interfaces.client;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.report.core.ReportStatus;
import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.report.interfaces.dto.InternalCreateReportRequestDto;
import com.whoseisthis.gateway.report.interfaces.dto.ReportResponseDto;
import com.whoseisthis.gateway.report.interfaces.dto.UpdateReportRequestDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface ReportApiClient {
    @GetExchange("/admin/reports")
    Mono<InternalSuccessResponse<PaginationResponse<ReportResponseDto>>> getAllForAdmin(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String reporter,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int limit,
            @RequestParam(required = false) List<String> sort);

    @PatchExchange("/admin/reports/{id}")
    Mono<InternalSuccessResponse<ReportResponseDto>> markReportAsResolved(@PathVariable Long id);

    @PostExchange("/reports")
    Mono<InternalSuccessResponse<ReportResponseDto>> create(
            @RequestBody InternalCreateReportRequestDto dto);

    @GetExchange("/reports")
    Mono<InternalSuccessResponse<PaginationResponse<ReportResponseDto>>> getAllForUser(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String reporter,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int limit,
            @RequestParam(required = false) List<String> sort);

    @GetExchange("/reports/{id}")
    Mono<InternalSuccessResponse<ReportResponseDto>> getReportById(@PathVariable Long id);

    @PutExchange("/reports/{id}/users/{userId}")
    Mono<InternalSuccessResponse<ReportResponseDto>> update(
            @PathVariable Long id, @PathVariable Long userId,
            @RequestBody UpdateReportRequestDto dto);
}
