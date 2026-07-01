package com.whoseisthis.gateway.auth.interfaces.service;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.auth.interfaces.client.AuthApiClient;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginResponseDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthApiClient authApiClient;

    public Mono<SignupResponseDto> signup(SignupRequestDto dto)
    {
        return authApiClient.signup(dto).map(InternalSuccessResponse::data);
    }

    public Mono<LoginResponseDto> login(LoginRequestDto dto)
    {
        return authApiClient.login(dto).map(InternalSuccessResponse::data);
    }
}
