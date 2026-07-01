package com.whoseisthis.reports.interfaces.service;

import com.whoseisthis.reports.common.exception.NotFoundError;
import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.infrastructure.repository.ReportRepository;
import com.whoseisthis.reports.infrastructure.repository.ReportSpecification;
import com.whoseisthis.reports.infrastructure.repository.ReporterRepository;
import com.whoseisthis.reports.interfaces.ReportMapper;
import com.whoseisthis.reports.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.reports.interfaces.dto.ReportFilter;
import com.whoseisthis.reports.interfaces.dto.UpdateReportRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReporterRepository reporterRepository;

    @Transactional
    public Report createReport(CreateReportRequestDto dto)
    {
        Reporter reporter = reporterRepository
                .findById(dto.reporter().id())
                .orElseGet(() -> {
                    Reporter r = new Reporter();
                    r.setUserId(dto.reporter().id());
                    r.setName(dto.reporter().name());
                    r.setEmail(dto.reporter().email());
                    return reporterRepository.save(r);
                });

        Report report = ReportMapper.create(dto);
        report.setReporter(reporter);
        Report result = reportRepository.save(report);
        log.info("Report created successfully, reportId={}, userId={}", result.getId(), reporter.getUserId());
        return result;
    }

    public Page<Report> getAllReports(ReportFilter filter, Pageable pageable)
    {
        var spec = Specification
                .where(ReportSpecification.titleContains(filter.title()))
                .and(ReportSpecification.locationContains(filter.location()))
                .and(ReportSpecification.reporter(filter.reporter()))
                .and(ReportSpecification.hasStatus(filter.status()))
                .and(ReportSpecification.hasType(filter.type()))
                .and(ReportSpecification.from(filter.from()))
                .and(ReportSpecification.to(filter.to()));
        return reportRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "reports", key = "'report:' + #id")
    public Report getReportById(Long id)
    {
        return reportRepository.findById(id).orElseThrow(NotFoundError::new);
    }

    @CacheEvict(value = "reports", key = "'report:' + #reportId")
    public Report updateReport(Long reportId, Long userId, UpdateReportRequestDto dto)
    {
        Report report = this.getReportById(reportId);
        if (!report.getReporter().getUserId().equals(userId) || report.getStatus() == ReportStatus.RESOLVED) {
            throw new NotFoundError();
        }
        Report updatedReport = ReportMapper.update(report, dto);
        Report result = reportRepository.save(updatedReport);
        log.info("Report updated successfully, reportId={}, userId={}", result.getId(), userId);
        return result;
    }

    @CacheEvict(value = "reports", key = "'report:' + #reportId")
    public Report markReportAsResolved(Long reportId)
    {
        Report report = this.getReportById(reportId);
        report.setStatus(ReportStatus.RESOLVED);
        Report result = reportRepository.save(report);
        log.info("Report marked as RESOLVED, reportId={}", reportId);
        return result;
    }
}
