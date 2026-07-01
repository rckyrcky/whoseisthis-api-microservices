package com.whoseisthis.reports.interfaces.service;

import com.whoseisthis.reports.common.exception.NotFoundError;
import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.infrastructure.repository.ReportRepository;
import com.whoseisthis.reports.infrastructure.repository.ReporterRepository;
import com.whoseisthis.reports.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.reports.interfaces.dto.ReportFilter;
import com.whoseisthis.reports.interfaces.dto.ReporterRequestDto;
import com.whoseisthis.reports.interfaces.dto.UpdateReportRequestDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ReporterRepository reporterRepository;

    @InjectMocks
    private ReportService reportService;

    @Nested
    class CreateReportTest {
        @Test
        void shouldCreateReport()
        {
            // Arrange
            var now = OffsetDateTime.now();
            var reporter = new Reporter(1L, "budi", "budi@test.com", null);
            var dto = new CreateReportRequestDto("lost book",
                    "a blue book",
                    "library",
                    ReportType.LOST,
                    new ReporterRequestDto(reporter.getUserId(), reporter.getName(), reporter.getEmail()));
            var report = new Report(1L,
                    dto.title(),
                    dto.description(),
                    dto.location(),
                    dto.type(),
                    ReportStatus.OPEN,
                    reporter,
                    now,
                    now);
            when(reporterRepository.findById(1L)).thenReturn(Optional.of(reporter));
            when(reportRepository.save(any(Report.class))).thenReturn(report);

            // Action
            var result = reportService.createReport(dto);

            // Assert
            assertEquals(result, report);
            verify(reportRepository).save(any(Report.class));
        }
    }

    @Nested
    class GetAllReportsTest {
        @Test
        void shouldGetAllReport()
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var expected = List.of(new Report(), new Report());
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<Report>(expected);
            when(reportRepository.findAll(ArgumentMatchers.<Specification<Report>>any(),
                    eq(pageable))).thenReturn(page);

            // Action
            var result = reportService.getAllReports(filter, pageable);

            // Assert
            assertEquals(2, result.getContent().size());
            verify(reportRepository).findAll(ArgumentMatchers.<Specification<Report>>any(), eq(pageable));
        }
    }

    @Nested
    class GetReportByIdTest {
        @Test
        void shouldThrowNotFoundErrorWhenDoesntExist()
        {
            // Arrange
            when(reportRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> reportService.getReportById(1L));
            verify(reportRepository).findById(1L);
        }

        @Test
        void shouldGetReportById()
        {
            // Arrange
            var expected = new Report();
            expected.setId(1L);
            when(reportRepository.findById(1L)).thenReturn(Optional.of(expected));

            // Action
            var result = reportService.getReportById(1L);

            // Assert
            assertEquals(expected.getId(), result.getId());
            verify(reportRepository).findById(1L);
        }

    }

    @Nested
    class UpdateReportTest {
        @Test
        void shouldThrowNotFoundErrorWhenReportIdDoesntExist()
        {
            // Arrange
            when(reportRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> reportService.getReportById(1L));
            verify(reportRepository).findById(1L);
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        void shouldThrowNotFoundErrorWhenUserIdDoesntMatch()
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var report = new Report();
            report.setId(1L);
            report.setReporter(reporter);
            report.setTitle("Book");
            var dto = new UpdateReportRequestDto("Laptop", null, null, ReportType.LOST);
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> reportService.updateReport(1L, 2L, dto));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        void shouldThrowNotFoundErrorWhenReportIsAlreadyResolved(){
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var report = new Report();
            report.setId(1L);
            report.setReporter(reporter);
            report.setTitle("Book");
            report.setStatus(ReportStatus.RESOLVED);
            var dto = new UpdateReportRequestDto("Laptop", null, null, ReportType.LOST);
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> reportService.updateReport(1L, 1L, dto));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        void shouldUpdateReport()
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var report = new Report();
            report.setId(1L);
            report.setReporter(reporter);
            report.setTitle("Book");
            var dto = new UpdateReportRequestDto("Laptop", null, null, ReportType.LOST);
            var expected = new Report();
            expected.setId(1L);
            expected.setReporter(reporter);
            expected.setTitle(dto.title());
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
            when(reportRepository.save(any(Report.class))).thenReturn(expected);

            // Action
            var result = reportService.updateReport(1L, 1L, dto);

            // Assert
            verify(reportRepository).save(any(Report.class));
            assertEquals(result.getTitle(), expected.getTitle());
        }
    }

    @Nested
    class MarkReportAsResolvedTest {
        @Test
        void shouldThrowNotFoundErrorWhenReportIdDoesntExist()
        {
            // Arrange
            when(reportRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> reportService.getReportById(1L));
            verify(reportRepository).findById(1L);
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        void shouldMarkReportAsResolved()
        {
            // Arrange
            var report = new Report();
            report.setId(1L);
            report.setStatus(ReportStatus.OPEN);
            var expected = new Report();
            expected.setId(1L);
            expected.setStatus(ReportStatus.RESOLVED);
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
            when(reportRepository.save(any(Report.class))).thenReturn(expected);

            // Action
            var result = reportService.markReportAsResolved(1L);

            // Assert
            assertEquals(ReportStatus.RESOLVED, result.getStatus());
            verify(reportRepository).save(report);
        }
    }
}
