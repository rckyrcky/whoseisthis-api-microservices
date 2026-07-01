package com.whoseisthis.gateway.auth.interfaces.client;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginResponseDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface AuthApiClient {
    @PostExchange("/auth/signup")
    Mono<InternalSuccessResponse<SignupResponseDto>> signup(@RequestBody SignupRequestDto dto);

    @PostExchange("/auth/login")
    Mono<InternalSuccessResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto dto);
}
