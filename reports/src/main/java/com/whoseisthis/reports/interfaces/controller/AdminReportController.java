package com.whoseisthis.reports.interfaces.controller;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.interfaces.ReportMapper;
import com.whoseisthis.reports.interfaces.dto.PaginationResponse;
import com.whoseisthis.reports.interfaces.dto.ReportFilter;
import com.whoseisthis.reports.interfaces.dto.ReportResponseDto;
import com.whoseisthis.reports.interfaces.service.ReportService;
import com.whoseisthis.reports.interfaces.utils.PaginationMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
@Validated
@RequiredArgsConstructor
public class AdminReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, PaginationResponse<ReportResponseDto>>> getAll(
            @Valid ReportFilter filter,
            Pageable pageable)
    {
        Page<Report> report = reportService.getAllReports(filter, pageable);
        var result = PaginationMapper.create(report, ReportMapper::get);
        return ResponseEntity.ok(Map.of("data", result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, ReportResponseDto>> markReportAsResolved(@PathVariable @Min(1) Long id)
    {
        Report report = reportService.markReportAsResolved(id);
        return ResponseEntity.ok(Map.of("data", new ReportResponseDto(report.getId())));
    }
}
