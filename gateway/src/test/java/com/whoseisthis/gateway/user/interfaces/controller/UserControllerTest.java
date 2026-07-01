package com.whoseisthis.gateway.user.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.infrastructure.JwtService;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import com.whoseisthis.gateway.user.core.UserRole;
import com.whoseisthis.gateway.user.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import com.whoseisthis.gateway.user.interfaces.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

@WebFluxTest(value = UserController.class)
@AutoConfigureWebTestClient
class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RateLimitService rateLimitService;

    private TestingAuthenticationToken getAuth()
    {
        var payload = new JwtPayload(1L, UserRole.USER);
        return new TestingAuthenticationToken(payload, null, "ROLE_USER");
    }

    private UserResponseDto getUsersDummy()
    {
        return new UserResponseDto(1L, "budi", "budi@test.com");
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldGetUserById() throws Exception
        {
            // Arrange
            when(userService.getUserByIdForUser(anyLong())).thenReturn(Mono.just(getUsersDummy()));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .get()
                         .uri("/users/me")
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.message").exists()
                         .jsonPath("$.data").exists();
            verify(userService).getUserByIdForUser(anyLong());
        }
    }

    @Nested
    class UpdateUserTest {
        @Test
        void shouldThrowErrorWhenBodyIsInvalid() throws Exception
        {
            // Arrange
            var dto = new UpdateUserRequestDto("budi", "aa", "");
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .put()
                         .uri("/users/me")
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(objectMapper.writeValueAsString(dto))
                         .exchange()
                         .expectStatus().isBadRequest()
                         .expectBody()
                         .jsonPath("$.data").doesNotExist();

            verify(userService, never()).updateUser(anyLong(), any(UpdateUserRequestDto.class));
        }

        @Test
        void shouldUpdateUser() throws Exception
        {
            // Arrange
            var dto = new UpdateUserRequestDto("budi@test.com", "new-password", "budi");
            when(userService.updateUser(anyLong(), any(UpdateUserRequestDto.class))).thenReturn(Mono.just(getUsersDummy()));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient.mutateWith(mockAuthentication(getAuth()))
                         .mutateWith(csrf())
                         .put()
                         .uri("/users/me")
                         .contentType(MediaType.APPLICATION_JSON)
                         .bodyValue(objectMapper.writeValueAsString(dto))
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody()
                         .jsonPath("$.data").exists();

            verify(userService).updateUser(anyLong(), any(UpdateUserRequestDto.class));
        }
    }
}
