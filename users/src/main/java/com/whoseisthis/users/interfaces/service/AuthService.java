package com.whoseisthis.users.interfaces.service;

import com.whoseisthis.users.application.JwtPayload;
import com.whoseisthis.users.common.exception.UserError;
import com.whoseisthis.users.core.User;
import com.whoseisthis.users.infrastructure.JwtService;
import com.whoseisthis.users.infrastructure.PasswordService;
import com.whoseisthis.users.interfaces.dto.LoginRequestDto;
import com.whoseisthis.users.interfaces.dto.LoginResponseDto;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public SignupResponseDto signup(SignupRequestDto dto)
    {
        User user = userService.createUser(dto);
        String token = jwtService.generate(new JwtPayload(user.getId(), user.getRole()));
        return new SignupResponseDto(user.getId(), token);
    }

    public LoginResponseDto login(LoginRequestDto dto)
    {
        User user = userService.getUserByEmailForLogin(dto.email());
        String hashedPassword = user.getPassword();
        if (!passwordService.compare(dto.password(), hashedPassword)) {
            log.warn("Login failed attempt, email={}", dto.email());
            throw new UserError("The email or password you entered is incorrect.");
        }
        String token = jwtService.generate(new JwtPayload(user.getId(), user.getRole()));
        return new LoginResponseDto(user.getId(), token);
    }
}
