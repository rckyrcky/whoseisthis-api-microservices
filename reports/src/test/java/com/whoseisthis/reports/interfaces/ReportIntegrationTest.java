package com.whoseisthis.reports.interfaces;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.infrastructure.repository.ReportRepository;
import com.whoseisthis.reports.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.reports.interfaces.dto.ReporterRequestDto;
import com.whoseisthis.reports.interfaces.dto.UpdateReportRequestDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReportIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReportRepository reportRepository;
    @Value("${gateway-token}")
    private String token;

    @Nested
    class AdminReportTest {
        @Nested
        class GetAllReportsTest {
            @Test
            void shouldGetAllReports() throws Exception
            {
                mockMvc.perform(get("/admin/reports")
                        .header("X-Gateway-Token", token)
                        .param("page", "1")
                        .param("limit", "20")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.data").exists()).andExpect(jsonPath(
                        "$.data.data").isArray()).andExpect(jsonPath("$.data.data", hasSize(20)));

            }

            @Test
            void shouldThrowBadRequestWhenFilterInvalid() throws Exception
            {
                mockMvc
                        .perform(get("/admin/reports")
                                .header("X-Gateway-Token", token)
                                .param("from", "invalid-date-format"))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc
                        .perform(get("/admin/reports"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class MarkReportAsResolvedTest {
            @Test
            void shouldMarkReportAsResolved() throws Exception
            {
                mockMvc
                        .perform(patch("/admin/reports/1").header("X-Gateway-Token", token))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").exists());

                Report report = reportRepository.findById(1L).orElseThrow();
                assertEquals(ReportStatus.RESOLVED, report.getStatus());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc
                        .perform(patch("/admin/reports/1"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowNotFoundWhenReportDoesNotExists() throws Exception
            {
                mockMvc
                        .perform(patch("/admin/reports/999999").header("X-Gateway-Token", token))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenParamInvalid() throws Exception
            {
                mockMvc
                        .perform(patch("/admin/reports/abc").header("X-Gateway-Token", token))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }
        }
    }

    @Nested
    class ReportTest {
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
                CreateReportRequestDto request = new CreateReportRequestDto("Test Title",
                        "Test Description",
                        "Test Location",
                        ReportType.LOST,
                        new ReporterRequestDto(reporter.getUserId(), reporter.getName(), reporter.getEmail()));

                mockMvc
                        .perform(post("/reports")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").exists());
            }

            @Test
            void shouldThrowBadRequestIfReportFieldInvalid() throws Exception
            {
                var reporter = new Reporter();
                reporter.setUserId(1L);
                reporter.setName("budi");
                reporter.setEmail("budi@test.com");
                CreateReportRequestDto request = new CreateReportRequestDto("",
                        null,
                        "Test Location",
                        ReportType.LOST,
                        new ReporterRequestDto(reporter.getUserId(), reporter.getName(), reporter.getEmail()));

                mockMvc
                        .perform(post("/reports")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                var reporter = new ReporterRequestDto(1L, "budi", "budi@test.com");
                CreateReportRequestDto request = new CreateReportRequestDto("Test Title",
                        "Test Description",
                        "Test Location",
                        ReportType.LOST, reporter);

                mockMvc
                        .perform(post("/reports")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class GetReportById {
            @Test
            void shouldGetReportById() throws Exception
            {
                mockMvc
                        .perform(get("/reports/1").header("X-Gateway-Token", token))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").exists());
            }

            @Test
            void shouldThrowBadRequestIfIdIsZero() throws Exception
            {
                mockMvc
                        .perform(get("/reports/0").header("X-Gateway-Token", token))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestIfParamInvalid() throws Exception
            {
                mockMvc
                        .perform(get("/reports/abcd").header("X-Gateway-Token", token))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowNotFoundIfReportDoesNotExist() throws Exception
            {
                mockMvc
                        .perform(get("/reports/99999999").header("X-Gateway-Token", token))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc
                        .perform(get("/reports/1"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class UpdateReportTest {
            @Test
            void shouldUpdateReport() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/1/users/2")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").exists());

                Report report = reportRepository.findById(1L).orElseThrow();
                assertEquals("Updated Title", report.getTitle());
            }

            @Test
            void shouldThrowNotFoundWhenUserNotCorrespondingReporter() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/1/users/1")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());

                Report report = reportRepository.findById(1L).orElseThrow();
                assertNotEquals("Updated Title", report.getTitle());
            }

            @Test
            void shouldThrowBadRequestIfIdIsZero() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/0/users/1")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestIfParamInvalid() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/abcdef/users/1")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowNotFoundWhenReportDoesNotExist() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/9999999999/users/1")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestIfBodyIsInvalid() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto(null, null, null, null);

                String originalTitle = reportRepository.findById(1L).orElseThrow().getTitle();

                mockMvc
                        .perform(put("/reports/1/users/1")
                                .header("X-Gateway-Token", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());

                Report report = reportRepository.findById(1L).orElseThrow();
                assertEquals(originalTitle, report.getTitle());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                UpdateReportRequestDto request = new UpdateReportRequestDto("Updated Title",
                        "Updated Description",
                        "Updated Location",
                        ReportType.LOST);

                mockMvc
                        .perform(put("/reports/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data").doesNotExist());
            }
        }
    }
}
