package com.whoseisthis.gateway.report.interfaces.controller;

import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.interfaces.dto.response.SuccessResponse;
import com.whoseisthis.gateway.report.interfaces.dto.ReportFilter;
import com.whoseisthis.gateway.report.interfaces.dto.ReportResponseDto;
import com.whoseisthis.gateway.report.interfaces.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Admin - Reports", description = "Admin APIs for managing reports")
@RestController
@RequestMapping("/admin/reports")
@Validated
@RequiredArgsConstructor
public class AdminReportController {
    private final ReportService reportService;

    @Operation(summary = "Get all reports", description = "Retrieve a paginated list of reports with optional " +
            "filtering")
    @GetMapping
    public Mono<ResponseEntity<SuccessResponse<PaginationResponse<ReportResponseDto>>>> getAll(
            @Valid ReportFilter filter, Pageable pageable)
    {
        return reportService
                .getAllReportsForAdmin(filter, pageable)
                .map(result -> ResponseEntity.ok(new SuccessResponse<>("Data retrieved successfully", result)));
    }

    @Operation(summary = "Mark report as resolved", description = "Update a report status to resolved")
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<SuccessResponse<ReportResponseDto>>> markReportAsResolved(@PathVariable @Min(1) Long id)
    {
        return reportService.markReportAsResolved(id).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Data updated successfully",
                result)));
    }
}
