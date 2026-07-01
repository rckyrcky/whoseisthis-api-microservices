package com.whoseisthis.gateway.report.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.infrastructure.JwtService;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.report.core.ReportStatus;
import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.report.interfaces.dto.ReportFilter;
import com.whoseisthis.gateway.report.interfaces.dto.ReportResponseDto;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

@WebFluxTest(value = AdminReportController.class)
@AutoConfigureWebTestClient
class AdminReportControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private ReportService reportService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RateLimitService rateLimitService;

    private TestingAuthenticationToken getAuth()
    {
        var payload = new JwtPayload(1L, UserRole.ADMIN);
        return new TestingAuthenticationToken(payload, null, "ROLE_ADMIN");
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
    class GetAllReportsTest {
        @Test
        void shouldGetAllReports() throws Exception
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var page = new PageImpl<ReportResponseDto>(List.of(getDummyReport()));
            var apiResponse = new PaginationResponse<>(page.stream().toList(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages());
            when(reportService.getAllReportsForAdmin(any(ReportFilter.class),
                    any(PageRequest.class))).thenReturn(Mono.just(apiResponse));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri(uriBuilder ->
                                 uriBuilder.path("/admin/reports")
                                           .queryParam("title", filter.title())
                                           .queryParam("page", "1")
                                           .queryParam("size", "100")
                                           .build()
                             )
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody().jsonPath("$.data").exists();

            verify(reportService).getAllReportsForAdmin(any(ReportFilter.class), any(PageRequest.class));
        }
    }

    @Nested
    class MarkReportAsResolvedTest {
        @Test
        void shouldThrowBadRequestIfIdIsZero() throws Exception
        {
            // Arrange
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .patch()
                         .uri("/admin/reports/{id}", 0L)
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody().jsonPath("$.data").doesNotExist();

            verify(reportService, never()).markReportAsResolved(anyLong());
        }

        @Test
        void shouldMarkReportAsResolved() throws Exception
        {
            // Arrange
            when(reportService.markReportAsResolved(1L)).thenReturn(Mono.just(getDummyReport()));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .patch()
                         .uri("/admin/reports/{id}", 1L)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody().jsonPath("$.data").exists()
                         .jsonPath("$.data.id").isEqualTo(1);

            verify(reportService).markReportAsResolved(anyLong());
        }
    }
}
