package com.whoseisthis.reports.interfaces.controller;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.interfaces.dto.ReportFilter;
import com.whoseisthis.reports.interfaces.service.ReportService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReportController.class)
class AdminReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReportService reportService;
    @MockitoBean
    private CacheManager cacheManager;

    private TestingAuthenticationToken getAuth()
    {
        return new TestingAuthenticationToken("gateway", null, "ROLE_GATEWAY");
    }

    private List<Report> getReportsDummy()
    {
        var reporter = new Reporter();
        reporter.setUserId(1L);
        reporter.setName("budi");
        reporter.setEmail("budi@test.com");
        var reports = new ArrayList<Report>();
        for (int i = 1; i < 10; i++) {
            var report = new Report();
            report.setId((long) i);
            report.setReporter(reporter);
            report.setTitle("lost book");
            report.setDescription("black and white");
            report.setLocation("cafeteria");
            report.setType(ReportType.LOST);
            report.setStatus(ReportStatus.OPEN);
            report.setCreatedAt(OffsetDateTime.now());
            reports.add(report);
        }
        return reports;
    }

    @Nested
    class GetAllReportsTest {
        @Test
        void shouldGetAllReports() throws Exception
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var pageable = PageRequest.of(1, 100);
            var page = new PageImpl<Report>(getReportsDummy());
            when(reportService.getAllReports(filter, pageable)).thenReturn(page);

            // Assert
            mockMvc.perform(get("/admin/reports")
                           .with(authentication(getAuth()))
                           .queryParam("title", filter.title())
                           .queryParam("page", "1")
                           .queryParam("size", "100"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists());
            verify(reportService).getAllReports(filter, pageable);
        }
    }

    @Nested
    class MarkReportAsResolvedTest {
        @Test
        void shouldThrowBadRequestIfIdIsZero() throws Exception
        {
            // Assert
            mockMvc.perform(patch("/admin/reports/{id}", 0L)
                           .with(csrf())
                           .with(authentication(getAuth())))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.data").doesNotExist());
            verify(reportService, never()).markReportAsResolved(1L);
        }

        @Test
        void shouldMarkReportAsResolved() throws Exception
        {
            // Arrange
            var report = new Report();
            report.setId(1L);
            when(reportService.markReportAsResolved(1L)).thenReturn(report);

            // Assert
            mockMvc.perform(patch("/admin/reports/{id}", 1L)
                           .with(csrf())
                           .with(authentication(getAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists())
                   .andExpect(jsonPath("$.data.id").value(1));
            verify(reportService).markReportAsResolved(1L);
        }
    }
}
