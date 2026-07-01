package com.whoseisthis.gateway.user.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.infrastructure.JwtService;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.user.core.UserRole;
import com.whoseisthis.gateway.user.interfaces.dto.UserFilter;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import com.whoseisthis.gateway.user.interfaces.service.UserService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

@WebFluxTest(value = AdminUserController.class)
@AutoConfigureWebTestClient
class AdminUserControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RateLimitService rateLimitService;


    private TestingAuthenticationToken getAuth()
    {
        var payload = new JwtPayload(1L, UserRole.ADMIN);
        return new TestingAuthenticationToken(payload, null, "ROLE_ADMIN");
    }

    private UserResponseDto getUsersDummy()
    {
        return new UserResponseDto(1L, "budi", "budi@test.com");
    }

    @Nested
    class GetAllUsersTest {
        @Test
        void shouldGetAllUsers() throws Exception
        {
            // Arrange
            var filter = new UserFilter("budi", null);
            var users = getUsersDummy();
            var expected = new PageImpl<UserResponseDto>(List.of(users));
            when(userService.getAllUsers(any(UserFilter.class), any(PageRequest.class))).thenReturn(Mono.just(new PaginationResponse<>(expected
                    .stream()
                    .toList(),
                    expected.getNumber(), expected.getSize(), expected.getTotalElements(), expected.getTotalPages())));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri(uriBuilder -> uriBuilder
                                 .path("/admin/users")
                                 .queryParam("name", filter.name())
                                 .queryParam("page", "1")
                                 .queryParam("size", "100").build()
                             )
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists();

            verify(userService).getAllUsers(any(UserFilter.class), any(PageRequest.class));
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri("/admin/users/{id}", 0)
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody()
                         .jsonPath("$.data").doesNotExist();

            verify(userService, never()).getUserByIdForAdmin(anyLong());
        }

        @Test
        void shouldGetUserByIdForAdmin() throws Exception
        {
            // Arrange
            when(userService.getUserByIdForAdmin(1L)).thenReturn(Mono.just(getUsersDummy()));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri("/admin/users/{id}", 1)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists();

            verify(userService).getUserByIdForAdmin(anyLong());
        }
    }

    @Nested
    class ResetUserPasswordTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Arrange
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .patch()
                         .uri("/admin/users/{id}", 0)
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody()
                         .jsonPath("$.data").doesNotExist();
            verify(userService, never()).resetUserPasswordToEmail(0L);
        }

        @Test
        void shouldResetUserPassword() throws Exception
        {
            // Arrange
            when(userService.resetUserPasswordToEmail(1L)).thenReturn(Mono.just(getUsersDummy()));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .patch()
                         .uri("/admin/users/{id}", 1)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists();

            verify(userService).resetUserPasswordToEmail(anyLong());
        }
    }
}
