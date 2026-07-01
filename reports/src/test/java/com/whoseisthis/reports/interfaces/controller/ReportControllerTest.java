package com.whoseisthis.reports.interfaces.controller;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.reports.interfaces.dto.ReportFilter;
import com.whoseisthis.reports.interfaces.dto.ReporterRequestDto;
import com.whoseisthis.reports.interfaces.dto.UpdateReportRequestDto;
import com.whoseisthis.reports.interfaces.service.ReportService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReportService reportService;
    @Autowired
    private ObjectMapper objectMapper;
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
    class CreateReportTest {
        @Test
        void shouldCreateReport() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var dto = new CreateReportRequestDto("lost book",
                    "black and white",
                    "cafeteria",
                    ReportType.LOST,
                    new ReporterRequestDto(reporter.getUserId(), reporter.getName(), reporter.getEmail()));
            var report = new Report();
            report.setId(1L);
            report.setTitle(dto.title());
            report.setDescription(dto.description());
            report.setLocation(dto.location());
            report.setReporter(reporter);
            when(reportService.createReport(any(CreateReportRequestDto.class)))
                    .thenReturn(report);

            // Assert
            mockMvc.perform(post("/reports")
                           .with(csrf())
                           .with(authentication(getAuth()))
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.data").exists())
                   .andExpect(jsonPath("$.data.id").value(1L));
            verify(reportService).createReport(dto);
        }
    }

    @Nested
    class GetAllReportsTest {
        @Test
        void shouldGetAllReports() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var pageable = PageRequest.of(1, 100);
            var page = new PageImpl<Report>(getReportsDummy());
            when(reportService.getAllReports(filter, pageable)).thenReturn(page);

            // Assert
            mockMvc.perform(get("/reports")
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
    class GetReportByIdTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");

            // Assert
            mockMvc
                    .perform(get("/reports/{id}", 0L).with(authentication(getAuth())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.data").doesNotExist());
            verify(reportService, never()).getReportById(1L);
        }

        @Test
        void shouldGetReportById() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var report = new Report();
            report.setId(1L);
            report.setTitle("lost book");
            report.setDescription("black and white");
            report.setLocation("cafeteria");
            report.setType(ReportType.LOST);
            report.setStatus(ReportStatus.OPEN);
            report.setReporter(reporter);
            report.setCreatedAt(OffsetDateTime.now());
            when(reportService.getReportById(1L)).thenReturn(report);

            // Assert
            mockMvc
                    .perform(get("/reports/{id}", 1L).with(authentication(getAuth())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.title").exists())
                    .andExpect(jsonPath("$.data.description").exists())
                    .andExpect(jsonPath("$.data.location").exists())
                    .andExpect(jsonPath("$.data.type").exists())
                    .andExpect(jsonPath("$.data.status").exists())
                    .andExpect(jsonPath("$.data.reporter").exists())
                    .andExpect(jsonPath("$.data.createdAt").exists());
            verify(reportService).getReportById(1L);
        }
    }

    @Nested
    class UpdateReportTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var dto = new UpdateReportRequestDto("book", "black", null, ReportType.LOST);

            // Assert
            mockMvc.perform(put("/reports/{id}/users/{userId}", 0L, 1L)
                                   .with(csrf())
                                   .with(authentication(getAuth()))
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content(objectMapper.writeValueAsString(dto))
                           )
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.data").doesNotExist());
            verify(reportService, never()).updateReport(1L, 1L, dto);
        }

        @Test
        void shouldUpdateReport() throws Exception
        {
            // Arrange
            var reporter = new Reporter();
            reporter.setUserId(1L);
            reporter.setName("budi");
            reporter.setEmail("budi@test.com");
            var dto = new UpdateReportRequestDto("book", "black", null, ReportType.LOST);
            var report = new Report();
            report.setId(1L);
            when(reportService.updateReport(1L, 1L, dto)).thenReturn(report);

            // Assert
            mockMvc.perform(put("/reports/{id}/users/{userId}", 1L, 1L)
                                   .with(csrf())
                                   .with(authentication(getAuth()))
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content(objectMapper.writeValueAsString(dto))
                           )
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists())
                   .andExpect(jsonPath("$.data.id").value(1));
            verify(reportService).updateReport(1L, 1L, dto);
        }
    }
}
