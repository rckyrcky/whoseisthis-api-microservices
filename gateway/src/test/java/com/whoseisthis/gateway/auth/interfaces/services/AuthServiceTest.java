package com.whoseisthis.gateway.auth.interfaces.services;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.auth.interfaces.client.AuthApiClient;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginResponseDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupResponseDto;
import com.whoseisthis.gateway.auth.interfaces.service.AuthService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthApiClient authApiClient;

    @InjectMocks
    private AuthService authService;


    @Nested
    class SignupTest {
        @Test
        void shouldSignupUser()
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "budi");
            var expected = new SignupResponseDto(1L, "token");
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(authApiClient.signup(dto)).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(authService.signup(dto))
                        .expectNext(expected)
                        .verifyComplete();

            verify(authApiClient).signup(dto);
        }
    }

    @Nested
    class LoginTest {
        @Test
        void shouldLoginUser()
        {
            // Arrange
            var dto = new LoginRequestDto("budi@test.com", "halo12345");
            var expected = new LoginResponseDto(1L, "token");
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(authApiClient.login(dto)).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(authService.login(dto))
                        .assertNext(result -> {
                            assertEquals(expected.id(), result.id());
                            assertEquals(expected.token(), result.token());
                        })
                        .verifyComplete();

            verify(authApiClient).login(dto);
        }
    }
}
