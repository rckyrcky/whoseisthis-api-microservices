package com.whoseisthis.users.interfaces.controller;


import com.whoseisthis.users.core.User;
import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.users.interfaces.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CacheManager cacheManager;

    private TestingAuthenticationToken getAuth()
    {
        return new TestingAuthenticationToken("gateway", null, "ROLE_GATEWAY");
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldGetUserById() throws Exception
        {
            // Arrange
            var user = new User();
            user.setId(1L);
            user.setEmail("budi@test.com");
            user.setName("budi");
            when(userService.getUserById(1L)).thenReturn(user);

            // Assert
            mockMvc
                    .perform(get("/users/me/1").with(authentication(getAuth())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(user.getId()))
                    .andExpect(jsonPath("$.data.name").value(user.getName()))
                    .andExpect(jsonPath("$.data.email").value(user.getEmail()));
            verify(userService).getUserById(1L);
        }
    }

    @Nested
    class UpdateUserTest {
        @Test
        void shouldThrowErrorWhenBodyIsInvalid() throws Exception
        {
            // Arrange
            var user = new User();
            user.setId(1L);
            user.setName("budi");
            var dto = new UpdateUserRequestDto("budi", "aa", "");

            // Assert
            mockMvc.perform(put("/users/me/1")
                           .with(authentication(getAuth()))
                           .with(csrf())
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.data").doesNotExist());
            verify(userService, never()).updateUser(1L, dto);
        }

        @Test
        void shouldUpdateUser() throws Exception
        {
            // Arrange
            var user = new User();
            user.setId(1L);
            user.setName("budi");
            var dto = new UpdateUserRequestDto("budi@test.com", "new-password", "budi");
            when(userService.updateUser(1L, dto)).thenReturn(user);

            // Assert
            mockMvc.perform(put("/users/me/1")
                           .with(authentication(getAuth()))
                           .with(csrf())
                           .contentType("application/json")
                           .content(objectMapper.writeValueAsString(dto)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(1L));
            verify(userService).updateUser(1L, dto);
        }
    }
}
