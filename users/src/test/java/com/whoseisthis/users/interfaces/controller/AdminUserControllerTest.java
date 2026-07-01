package com.whoseisthis.users.interfaces.controller;

import com.whoseisthis.users.core.User;
import com.whoseisthis.users.interfaces.dto.UserFilter;
import com.whoseisthis.users.interfaces.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CacheManager cacheManager;

    private TestingAuthenticationToken getAuth()
    {
        return new TestingAuthenticationToken("gateway", null, "ROLE_GATEWAY");
    }

    private List<User> getUsersDummy()
    {
        var users = new ArrayList<User>();
        for (int i = 1; i < 10; i++) {
            var user = new User();
            user.setId((long) i);
            user.setName("budi");
            users.add(user);
        }
        return users;
    }

    @Nested
    class GetAllUsersTest {
        @Test
        void shouldGetAllUsers() throws Exception
        {
            // Arrange
            var filter = new UserFilter("budi", null);
            var pageable = PageRequest.of(1, 100);
            var users = getUsersDummy();
            var expected = new PageImpl<>(users);
            when(userService.getAllUsers(filter, pageable)).thenReturn(expected);

            // Assert
            mockMvc.perform(get("/admin/users")
                           .with(authentication(getAuth()))
                           .queryParam("name", filter.name())
                           .queryParam("page", "1")
                           .queryParam("size", "100"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists());

            verify(userService).getAllUsers(filter, pageable);
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Assert
            mockMvc.perform(get("/admin/users/{id}", 0)
                           .with(authentication(getAuth())))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.data").doesNotExist());
            verify(userService, never()).getUserById(0L);
        }

        @Test
        void shouldGetUserById() throws Exception
        {
            // Arrange
            var expected = new User();
            expected.setId(1L);
            expected.setEmail("budi@test.com");
            expected.setName("budi");
            when(userService.getUserById(1L)).thenReturn(expected);

            // Assert
            mockMvc.perform(get("/admin/users/{id}", 1)
                           .with(authentication(getAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists())
                   .andExpect(jsonPath("$.data.id").value(1))
                   .andExpect(jsonPath("$.data.name").value(expected.getName()))
                   .andExpect(jsonPath("$.data.email").value(expected.getEmail()));
            verify(userService).getUserById(1L);
        }
    }

    @Nested
    class ResetUserPasswordTest {
        @Test
        void shouldThrowBadRequestWhenIdIsZero() throws Exception
        {
            // Assert
            mockMvc.perform(patch("/admin/users/{id}", 0)
                           .with(csrf())
                           .with(authentication(getAuth())))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.data").doesNotExist());
            verify(userService, never()).resetUserPasswordToEmail(0L);
        }

        @Test
        void shouldResetUserPassword() throws Exception
        {
            // Arrange
            var expected = new User();
            expected.setId(1L);
            expected.setEmail("budi@test.com");
            expected.setName("budi");
            expected.setPassword("budi@test.com");
            when(userService.resetUserPasswordToEmail(1L)).thenReturn(expected);

            // Assert
            mockMvc.perform(patch("/admin/users/{id}", 1)
                           .with(csrf())
                           .with(authentication(getAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").exists())
                   .andExpect(jsonPath("$.data.id").value(1));
            verify(userService).resetUserPasswordToEmail(1L);
        }
    }
}
