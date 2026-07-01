package com.whoseisthis.users.interfaces.services;

import com.whoseisthis.users.application.JwtPayload;
import com.whoseisthis.users.common.exception.UserError;
import com.whoseisthis.users.core.User;
import com.whoseisthis.users.infrastructure.JwtService;
import com.whoseisthis.users.infrastructure.PasswordService;
import com.whoseisthis.users.interfaces.dto.LoginRequestDto;
import com.whoseisthis.users.interfaces.dto.LoginResponseDto;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.SignupResponseDto;
import com.whoseisthis.users.interfaces.service.AuthService;
import com.whoseisthis.users.interfaces.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private AuthService authService;


    @Nested
    class SignupTest {
        @Test
        void shouldSignupUser()
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "budi");
            var user = new User();
            user.setId(1L);
            var expected = new SignupResponseDto(1L, "token");
            when(userService.createUser(dto)).thenReturn(user);
            when(jwtService.generate(any(JwtPayload.class))).thenReturn("token");

            // Action
            var result = authService.signup(dto);

            // Assert
            assertEquals(expected.id(), result.id());
            assertEquals(expected.token(), result.token());
            verify(userService).createUser(dto);
            verify(jwtService).generate(any(JwtPayload.class));
        }
    }

    @Nested
    class LoginTest {
        @Test
        void shouldThrowUserErrorWhenWrongPassword(){
            // Arrange
            var dto = new LoginRequestDto("budi@test.com", "halo12345");
            var user = new User();
            user.setEmail(dto.email());
            user.setPassword("hashed-password");
            when(userService.getUserByEmailForLogin(dto.email())).thenReturn(user);
            when(passwordService.compare(dto.password(), user.getPassword())).thenReturn(false);

            // Assert
            assertThrowsExactly(UserError.class, () -> authService.login(dto));
            verify(userService).getUserByEmailForLogin(dto.email());
            verify(passwordService).compare(dto.password(), user.getPassword());
            verify(jwtService, never()).generate(any(JwtPayload.class));
        }
        @Test
        void shouldLoginUser()
        {
            // Arrange
            var dto = new LoginRequestDto("budi@test.com", "halo12345");
            var user = new User();
            user.setId(1L);
            user.setEmail(dto.email());
            user.setPassword("hashed-password");
            var expected = new LoginResponseDto(1L, "token");
            when(userService.getUserByEmailForLogin(dto.email())).thenReturn(user);
            when(passwordService.compare(dto.password(), user.getPassword())).thenReturn(true);
            when(jwtService.generate(any(JwtPayload.class))).thenReturn("token");

            // Action
            var result = authService.login(dto);

            // Assert
            assertEquals(expected.id(), result.id());
            assertEquals(expected.token(), result.token());
            verify(userService).getUserByEmailForLogin(dto.email());
            verify(passwordService).compare(dto.password(), user.getPassword());
            verify(jwtService).generate(any(JwtPayload.class));
        }
    }

}
