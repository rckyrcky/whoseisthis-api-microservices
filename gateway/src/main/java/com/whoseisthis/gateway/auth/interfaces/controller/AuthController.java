package com.whoseisthis.gateway.auth.interfaces.controller;

import com.whoseisthis.gateway.auth.interfaces.dto.LoginRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.LoginResponseDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupRequestDto;
import com.whoseisthis.gateway.auth.interfaces.dto.SignupResponseDto;
import com.whoseisthis.gateway.auth.interfaces.service.AuthService;
import com.whoseisthis.gateway.interfaces.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Authentication", description = "APIs for authentication and account access")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Signup", description = "Create a new user account")
    @PostMapping("/signup")
    public Mono<ResponseEntity<SuccessResponse<SignupResponseDto>>> signup(@RequestBody @Valid SignupRequestDto dto)
    {
        return service.signup(dto).map(result -> {
            ResponseCookie cookie = ResponseCookie
                    .from("token", result.token())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(900)
                    .build();

            SuccessResponse<SignupResponseDto> response = new SuccessResponse<SignupResponseDto>("Signup success",
                    new SignupResponseDto(result.id()));

            return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                    response);
        });
    }

    @Operation(summary = "Login", description = "Authenticate a user and return access credentials")
    @PostMapping("/login")
    public Mono<ResponseEntity<SuccessResponse<LoginResponseDto>>> login(@RequestBody @Valid LoginRequestDto dto)
    {
        return service.login(dto).map(result -> {
            ResponseCookie cookie = ResponseCookie
                    .from("token", result.token())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(900)
                    .build();

            SuccessResponse<LoginResponseDto> response = new SuccessResponse<LoginResponseDto>("Login successful",
                    new LoginResponseDto(result.id()));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        });
    }

    @Operation(summary = "Logout", description = "Logout the authenticated user")
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout()
    {
        ResponseCookie cookie = ResponseCookie
                .from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        SuccessResponse<Void> response = new SuccessResponse<>("Logout successful");
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }
}
