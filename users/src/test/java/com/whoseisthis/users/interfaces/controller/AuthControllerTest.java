package com.whoseisthis.users.interfaces.controller;

import com.whoseisthis.users.interfaces.dto.LoginRequestDto;
import com.whoseisthis.users.interfaces.dto.LoginResponseDto;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.SignupResponseDto;
import com.whoseisthis.users.interfaces.service.AuthService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CacheManager cacheManager;

    @Nested
    class SignupTest {
        @Test
        void shouldSignup() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "budi");
            var result = new SignupResponseDto(1L, "token");
            when(authService.signup(dto)).thenReturn(result);

            // Assert
            mockMvc.perform(post("/auth/signup")
                           .with(csrf())
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.data.id").value(1))
                   .andExpect(jsonPath("$.data.token").value("token"));
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
            when(authService.login(dto)).thenReturn(result);

            // Assert
            mockMvc.perform(post("/auth/login")
                           .with(csrf())
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(1))
                   .andExpect(jsonPath("$.data.token").value("token"));
            verify(authService).login(dto);
        }
    }
}
