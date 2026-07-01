package com.whoseisthis.reports.interfaces.controller;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.interfaces.ReportMapper;
import com.whoseisthis.reports.interfaces.dto.*;
import com.whoseisthis.reports.interfaces.service.ReportService;
import com.whoseisthis.reports.interfaces.utils.PaginationMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports")
@Validated
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Map<String, ReportResponseDto>> create(
            @RequestBody @Valid CreateReportRequestDto dto)
    {
        Report report = reportService.createReport(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", new ReportResponseDto(report.getId())));
    }

    @GetMapping
    public ResponseEntity<Map<String, PaginationResponse<ReportResponseDto>>> getAll(
            @Valid ReportFilter filter,
            Pageable pageable)
    {
        Page<Report> report = reportService.getAllReports(filter, pageable);
        var result = PaginationMapper.create(report, ReportMapper::get);
        return ResponseEntity.ok(Map.of("data", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, ReportResponseDto>> getReportById(@PathVariable @Min(1) Long id)
    {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(Map.of("data", ReportMapper.get(report)));
    }

    @PutMapping("/{id}/users/{userId}")
    public ResponseEntity<Map<String, ReportResponseDto>> update(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long id, @RequestBody @Valid UpdateReportRequestDto dto)
    {
        Report report = reportService.updateReport(id, userId, dto);
        return ResponseEntity.ok(Map.of("data", new ReportResponseDto(report.getId())));
    }

}
