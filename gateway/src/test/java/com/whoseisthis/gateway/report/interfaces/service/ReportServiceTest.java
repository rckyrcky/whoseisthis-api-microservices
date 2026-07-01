package com.whoseisthis.gateway.report.interfaces.service;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.report.core.ReportStatus;
import com.whoseisthis.gateway.report.core.ReportType;
import com.whoseisthis.gateway.report.interfaces.client.ReportApiClient;
import com.whoseisthis.gateway.report.interfaces.dto.*;
import com.whoseisthis.gateway.user.interfaces.client.UserApiClient;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private UserApiClient userApiClient;
    @Mock
    private ReportApiClient reportApiClient;
    @InjectMocks
    private ReportService reportService;

    private ReportResponseDto getDummyReport()
    {
        return new ReportResponseDto(1L,
                "lost book",
                "blue book",
                "library",
                ReportType.LOST,
                ReportStatus.OPEN,
                new UserResponseDto(1L, "budi", "budi@test.com"),
                OffsetDateTime.now());
    }

    @Nested
    class CreateReportTest {
        @Test
        void shouldCreateReport()
        {
            // Arrange
            var dto = new CreateReportRequestDto("lost book", "a blue book", "library", ReportType.LOST);
            var userResponseDto = new UserResponseDto(1L, "budi", "budi@test.com");
            var internalDto = new InternalCreateReportRequestDto(dto.title(),
                    dto.description(),
                    dto.location(),
                    dto.type(),
                    userResponseDto);
            var expected = getDummyReport();
            var apiResponse2 = new InternalSuccessResponse<>(expected);
            when(reportApiClient.create(any(InternalCreateReportRequestDto.class))).thenReturn(Mono.just(apiResponse2));

            // Action + Assert
            StepVerifier.create(reportApiClient.create(internalDto)).expectNext(apiResponse2).verifyComplete();
        }
    }

    @Nested
    class GetAllReportsTest {
        @Test
        void shouldGetAllReportForAdmin()
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var expected = List.of(getDummyReport());
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<ReportResponseDto>(expected);
            var apiResponse = new PaginationResponse<ReportResponseDto>(expected,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages());
            when(reportApiClient.getAllForAdmin(anyString(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    anyInt(),
                    anyInt(),
                    anyList())).thenReturn(Mono.just(new InternalSuccessResponse<>(
                    apiResponse)));

            // Action + Assert
            StepVerifier
                    .create(reportApiClient.getAllForAdmin(filter.title(),
                            filter.location(),
                            filter.reporter(),
                            filter.status(),
                            filter.type(),
                            filter.from(),
                            filter.to(),
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            pageable
                                    .getSort()
                                    .stream()
                                    .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                                    .toList()))
                    .expectNext(new InternalSuccessResponse<>(apiResponse))
                    .verifyComplete();

        }

        @Test
        void shouldGetAllReportForUser()
        {
            // Arrange
            var filter = new ReportFilter("book", null, null, null, null, null, null);
            var expected = List.of(getDummyReport());
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<ReportResponseDto>(expected);
            var apiResponse = new PaginationResponse<ReportResponseDto>(expected,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages());
            when(reportApiClient.getAllForUser(anyString(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    anyInt(),
                    anyInt(),
                    anyList())).thenReturn(Mono.just(new InternalSuccessResponse<>(
                    apiResponse)));

            // Action + Assert
            StepVerifier
                    .create(reportApiClient.getAllForUser(filter.title(),
                            filter.location(),
                            filter.reporter(),
                            filter.status(),
                            filter.type(),
                            filter.from(),
                            filter.to(),
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            pageable
                                    .getSort()
                                    .stream()
                                    .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                                    .toList()))
                    .expectNext(new InternalSuccessResponse<>(apiResponse))
                    .verifyComplete();
        }
    }

    @Nested
    class GetReportByIdTest {
        @Test
        void shouldGetReportById()
        {
            // Arrange
            var expected = getDummyReport();
            InternalSuccessResponse<ReportResponseDto> apiResponse = new InternalSuccessResponse<>(expected);
            when(reportApiClient.getReportById(anyLong())).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(reportService.getReportById(1L)).expectNext(expected).verifyComplete();

            verify(reportApiClient).getReportById(anyLong());
        }

    }

    @Nested
    class UpdateReportTest {
        @Test
        void shouldUpdateReport()
        {
            // Arrange
            var dto = new UpdateReportRequestDto("Laptop", null, null, ReportType.LOST);
            var expected = getDummyReport();
            InternalSuccessResponse<ReportResponseDto> apiResponse = new InternalSuccessResponse<>(expected);
            when(reportApiClient.update(anyLong(), anyLong(), any(UpdateReportRequestDto.class))).thenReturn(Mono.just(
                    apiResponse));

            // Action + Assert
            StepVerifier.create(reportService.updateReport(1L, 1L, dto)).expectNext(expected).verifyComplete();
            verify(reportApiClient).update(anyLong(), anyLong(), any(UpdateReportRequestDto.class));
        }
    }

    @Nested
    class MarkReportAsResolvedTest {
        @Test
        void shouldMarkReportAsResolved()
        {
            // Arrange
            var expected = getDummyReport();
            InternalSuccessResponse<ReportResponseDto> apiResponse = new InternalSuccessResponse<>(expected);
            when(reportApiClient.markReportAsResolved(anyLong())).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(reportService.markReportAsResolved(1L)).expectNext(expected).verifyComplete();
            verify(reportApiClient).markReportAsResolved(anyLong());
        }
    }
}
