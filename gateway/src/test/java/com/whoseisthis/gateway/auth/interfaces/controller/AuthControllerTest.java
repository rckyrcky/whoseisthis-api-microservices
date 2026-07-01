package com.whoseisthis.gateway.auth.interfaces.controller;

import com.whoseisthis.gateway.auth.interfaces.dto.LoginRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginResponseDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupResponseDto;
import com.whoseisthis.gateway.auth.interfaces.service.AuthService;
import com.whoseisthis.gateway.infrastructure.JwtService;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(value = AuthController.class, excludeAutoConfiguration = ReactiveWebSecurityAutoConfiguration.class)
@AutoConfigureWebTestClient
class AuthControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RateLimitService rateLimitService;

    @Nested
    class SignupTest {
        @Test
        void shouldSignup() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "budi");
            var result = new SignupResponseDto(1L, "token");
            when(authService.signup(dto)).thenReturn(Mono.just(result));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient
                    .post()
                    .uri("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(dto))
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*token=token.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*HttpOnly.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*Secure.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*SameSite=Strict.*")
                    .expectBody()
                    .jsonPath("$.data.id")
                    .isEqualTo(1);

            verify(authService).signup(dto);
        }
    }

    @Nested
    class LoginTest {
        @Test
        void shouldLogin() throws Exception
        {
            // Arrange
            var dto = new LoginRequestDto("budi@test.com", "halo12345");
            var result = new LoginResponseDto(1L, "token");
            when(authService.login(any())).thenReturn(Mono.just(result));
            when(rateLimitService.allow(anyString(), anyLong(), anyLong())).thenReturn(Mono.just(true));

            // Assert
            webTestClient
                    .post()
                    .uri("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(dto))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*token=token.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*HttpOnly.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*Secure.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*SameSite=Strict.*")
                    .expectBody()
                    .jsonPath("$.data.id")
                    .isEqualTo(1);

            verify(authService).login(any(LoginRequestDto.class));
        }
    }

    @Nested
    class LogoutTest {
        @Test
        void shouldLogout() throws Exception
        {
            // Assert
            webTestClient
                    .post()
                    .uri("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .exists(HttpHeaders.SET_COOKIE)
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*Max-Age=0.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*HttpOnly.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*Secure.*")
                    .expectHeader()
                    .valueMatches(HttpHeaders.SET_COOKIE, ".*SameSite=Strict.*")
                    .expectBody()
                    .jsonPath("$.message")
                    .exists();
        }
    }
}
