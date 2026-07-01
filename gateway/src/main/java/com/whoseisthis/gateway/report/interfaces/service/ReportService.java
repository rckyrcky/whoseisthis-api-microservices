package com.whoseisthis.gateway.report.interfaces.service;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.report.interfaces.client.ReportApiClient;
import com.whoseisthis.gateway.report.interfaces.dto.*;
import com.whoseisthis.gateway.user.interfaces.client.UserApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ReportApiClient reportApiClient;
    private final UserApiClient userApiClient;

    public Mono<ReportResponseDto> createReport(Long userId, CreateReportRequestDto dto)
    {
        return userApiClient
                .getUserByIdForUser(userId)
                .map(userResult -> new InternalCreateReportRequestDto(dto.title(),
                         dto.description(),
                         dto.location(),
                         dto.type(),
                         userResult.data()))
                .flatMap(reportApiClient::create)
                .map(InternalSuccessResponse::data);
    }

    public Mono<PaginationResponse<ReportResponseDto>> getAllReportsForAdmin(ReportFilter filter, Pageable pageable)
    {
        return reportApiClient.getAllForAdmin(filter.title(),
                filter.location(),
                filter.reporter(),
                filter.status(),
                filter.type(),
                filter.from(),
                filter.to(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable
                        .getSort()
                        .stream()
                        .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                        .toList()).map(
                InternalSuccessResponse::data);
    }

    public Mono<PaginationResponse<ReportResponseDto>> getAllReportsForUser(ReportFilter filter, Pageable pageable)
    {
        return reportApiClient.getAllForUser(filter.title(),
                filter.location(),
                filter.reporter(),
                filter.status(),
                filter.type(),
                filter.from(),
                filter.to(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable
                        .getSort()
                        .stream()
                        .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                        .toList()).map(InternalSuccessResponse::data);
    }

    public Mono<ReportResponseDto> getReportById(Long id)
    {
        return reportApiClient.getReportById(id).map(InternalSuccessResponse::data);
    }

    public Mono<ReportResponseDto> updateReport(Long reportId, Long userId, UpdateReportRequestDto dto)
    {
        return reportApiClient.update(reportId, userId, dto).map(InternalSuccessResponse::data);
    }

    public Mono<ReportResponseDto> markReportAsResolved(Long reportId)
    {
        return reportApiClient.markReportAsResolved(reportId).map(InternalSuccessResponse::data);
    }
}
