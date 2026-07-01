package com.whoseisthis.reports.interfaces;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.reports.interfaces.dto.ReportResponseDto;
import com.whoseisthis.reports.interfaces.dto.ReporterResponseDto;
import com.whoseisthis.reports.interfaces.dto.UpdateReportRequestDto;

public final class ReportMapper {
    private ReportMapper()
    {
    }

    public static Report create(CreateReportRequestDto dto)
    {
        Report r = new Report();
        r.setTitle(dto.title());
        r.setDescription(dto.description());
        r.setLocation(dto.location());
        r.setType(dto.type());
        r.setStatus(ReportStatus.OPEN);
        return r;
    }

    public static Report update(Report report, UpdateReportRequestDto dto)
    {
        if (dto.title() != null && !dto.title().isBlank()) {
            report.setTitle(dto.title());
        }
        if (dto.description() != null && !dto.description().isBlank()) {
            report.setDescription(dto.description());
        }
        if (dto.location() != null && !dto.location().isBlank()) {
            report.setLocation(dto.location());
        }
        if (dto.type() != null) {
            report.setType(dto.type());
        }
        return report;
    }

    public static ReportResponseDto get(Report report)
    {
        ReporterResponseDto reporter = new ReporterResponseDto(
                report.getReporter().getUserId(),
                report.getReporter().getName(),
                report.getReporter().getEmail());
        return new ReportResponseDto(report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getLocation(),
                report.getType(),
                report.getStatus(),
                reporter,
                report.getCreatedAt());
    }
}
