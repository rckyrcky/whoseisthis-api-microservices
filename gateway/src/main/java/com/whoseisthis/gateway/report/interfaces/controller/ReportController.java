package com.whoseisthis.gateway.report.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.interfaces.dto.response.SuccessResponse;
import com.whoseisthis.gateway.report.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.gateway.report.interfaces.dto.ReportFilter;
import com.whoseisthis.gateway.report.interfaces.dto.ReportResponseDto;
import com.whoseisthis.gateway.report.interfaces.dto.UpdateReportRequestDto;
import com.whoseisthis.gateway.report.interfaces.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Reports", description = "APIs for user report management")
@RestController
@RequestMapping("/reports")
@Validated
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "Create a report", description = "Create a new report")
    @PostMapping
    public Mono<ResponseEntity<SuccessResponse<ReportResponseDto>>> create(
            @AuthenticationPrincipal JwtPayload payload,
            @RequestBody @Valid CreateReportRequestDto dto)
    {
        return reportService.createReport(payload.id(), dto).map(result -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse<>("Report created successfully", result)));
    }

    @Operation(summary = "Get all reports", description = "Retrieve a paginated list of reports with optional " +
            "filtering")
    @GetMapping
    public Mono<ResponseEntity<SuccessResponse<PaginationResponse<ReportResponseDto>>>> getAll(
            @Valid ReportFilter filter, Pageable pageable)
    {
        return reportService
                .getAllReportsForUser(filter, pageable)
                .map(result -> ResponseEntity.ok(new SuccessResponse<>("Data retrieved successfully", result)));
    }

    @Operation(summary = "Get report by ID", description = "Retrieve report details by report ID")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SuccessResponse<ReportResponseDto>>> getReportById(@PathVariable @Min(1) Long id)
    {
        return reportService
                .getReportById(id)
                .map(result -> ResponseEntity.ok(new SuccessResponse<>("Data retrieved " + "successfully", result)));
    }

    @Operation(summary = "Update a report", description = "Update an existing report")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SuccessResponse<ReportResponseDto>>> update(
            @AuthenticationPrincipal JwtPayload payload, @PathVariable @Min(1) Long id,
            @RequestBody @Valid UpdateReportRequestDto dto)
    {
        return reportService.updateReport(id, payload.id(), dto).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Data updated successfully",
                result)));
    }
}
