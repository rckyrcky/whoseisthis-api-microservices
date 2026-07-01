package com.whoseisthis.users.interfaces.controller;

import com.whoseisthis.users.interfaces.dto.LoginRequestDto;
import com.whoseisthis.users.interfaces.dto.LoginResponseDto;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.SignupResponseDto;
import com.whoseisthis.users.interfaces.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, SignupResponseDto>> signup(@RequestBody @Valid SignupRequestDto dto)
    {
        SignupResponseDto result = service.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", result));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto dto)
    {
        LoginResponseDto result = service.login(dto);
        return ResponseEntity.ok(Map.of("data", result));
    }
}
