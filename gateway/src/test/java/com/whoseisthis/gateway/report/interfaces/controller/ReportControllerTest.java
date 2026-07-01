package com.whoseisthis.gateway.report.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.infrastructure.JwtService;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.report.core.ReportStatus;
import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.report.interfaces.dto.CreateReportRequestDto;
import com.whoseisthis.gateway.report.interfaces.dto.ReportFilter;
import com.whoseisthis.gateway.report.interfaces.dto.ReportResponseDto;
import com.whoseisthis.gateway.report.interfaces.dto.UpdateReportRequestDto;
import com.whoseisthis.gateway.report.interfaces.service.ReportService;
import com.whoseisthis.gateway.user.core.UserRole;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

@WebFluxTest(value = ReportController.class)
@AutoConfigureWebTestClient
class ReportControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private ReportService reportService;
    @MockitoBean
    private RateLimitService rateLimitService;
    @MockitoBean
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;

    private TestingAuthenticationToken getAuth()
    {
        var payload = new JwtPayload(9L, UserRole.USER);
        return new TestingAuthenticationToken(payload, null, "ROLE_USER");
    }

    private ReportResponseDto getDummyReport()
    {
        return new ReportResponseDto(1L,
                "lost book",
                "blue book",
                "library",
                ReportType.LOST,
                ReportStatus.OPEN,
                new UserResponseDto(1L, "budi", "budi@test.com"), OffsetDateTime.now());
    }

    @Nested
    class CreateReportTest {
        @Test
        void shouldCreateReport() throws Exception
        {
            // Arrange
            var dto = new CreateReportRequestDto("lost book", "black and white", "cafeteria", ReportType.LOST);
            when(reportService.createReport(any(), any(CreateReportRequestDto.class))).thenReturn(Mono.just(getDummyReport()));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .post()
                         .uri("/reports")
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(objectMapper.writeValueAsString(dto))
                         .exchange()
                         .expectStatus().isCreated()
                         .expectBody().jsonPath("$.data").exists()
                         .jsonPath("$.data.id").isEqualTo(1L);

            verify(reportService).createReport(anyLong(), any(CreateReportRequestDto.class));
        }
    }

    @Nested
    class GetAllReportsTest {
        @Test
        void shouldGetAllReports() throws Exception
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var page = new PageImpl<ReportResponseDto>(List.of(getDummyReport()));
            var apiResponse = new PaginationResponse<>(page.stream().toList(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages());
            when(reportService.getAllReportsForUser(any(ReportFilter.class), any(PageRequest.class))).thenReturn(Mono.just(apiResponse));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri(uriBuilder ->
                                 uriBuilder.path("/reports")
                                           .queryParam("title", filter.title())
                                           .queryParam("page", "1")
                                           .queryParam("size", "100")
                                           .build()
                             )
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody().jsonPath("$.data").exists();

            verify(reportService).getAllReportsForUser(any(ReportFilter.class), any(PageRequest.class));
        }
    }

    @Nested
    class GetReportByIdTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri("/reports/{id}", 0L)
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody().jsonPath("$.data").doesNotExist();

            verify(reportService, never()).getReportById(anyLong());
        }

        @Test
        void shouldGetReportById() throws Exception
        {
            // Arrange
            when(reportService.getReportById(anyLong())).thenReturn(Mono.just(getDummyReport()));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri("/reports/{id}", 1L)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists()
                         .jsonPath("$.data.title").exists()
                         .jsonPath("$.data.description").exists()
                         .jsonPath("$.data.location").exists()
                         .jsonPath("$.data.type").exists()
                         .jsonPath("$.data.status").exists()
                         .jsonPath("$.data.reporter").exists()
                         .jsonPath("$.data.createdAt").exists();

            verify(reportService).getReportById(anyLong());
        }
    }

    @Nested
    class UpdateReportTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Arrange
            var dto = new UpdateReportRequestDto("book", "black", null, ReportType.LOST);
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .put()
                         .uri("/reports/{id}", 0L)
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(objectMapper.writeValueAsString(dto))
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody()
                         .jsonPath("$.data").doesNotExist();

            verify(reportService, never()).updateReport(anyLong(), anyLong(), any(UpdateReportRequestDto.class));
        }

        @Test
        void shouldUpdateReport() throws Exception
        {
            // Arrange
            var dto = new UpdateReportRequestDto("book", "black", null, ReportType.LOST);
            when(reportService.updateReport(anyLong(), anyLong(), any(UpdateReportRequestDto.class))).thenReturn(Mono.just(getDummyReport()));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .put()
                         .uri("/reports/{id}", 1L)
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(objectMapper.writeValueAsString(dto))
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists()
                         .jsonPath("$.data.id").isEqualTo(1);

            verify(reportService).updateReport(anyLong(), anyLong(), any(UpdateReportRequestDto.class));
        }
    }
}
