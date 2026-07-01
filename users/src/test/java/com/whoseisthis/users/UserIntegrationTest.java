package com.whoseisthis.users;

import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${gateway-token}")
    private String token;

    @Nested
    class AdminUserTest {
        @Nested
        class GetAllUsersTest {
            @Test
            void shouldGetAllUsers() throws Exception
            {
                mockMvc.perform(get("/admin/users")
                               .header("X-Gateway-Token", token)
                               .param("page", "1")
                               .param("limit", "20"))
                       .andExpect(status().isOk())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").exists())
                       .andExpect(jsonPath("$.data.data").isArray());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc.perform(get("/admin/users"))
                       .andExpect(status().isUnauthorized())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenFilterInvalid() throws Exception
            {
                mockMvc.perform(get("/admin/users")
                               .header("X-Gateway-Token", token)
                               .param("name", "this-name-is-way-too-long-to-be-accepted-by-the-filter-max-20"))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class GetUserById {
            @Test
            void shouldReturnUserById() throws Exception
            {
                mockMvc.perform(get("/admin/users/1")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isOk())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").exists());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc.perform(get("/admin/users/1"))
                       .andExpect(status().isUnauthorized())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenIdIsZero() throws Exception
            {
                mockMvc.perform(get("/admin/users/0")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenIdIsInvalid() throws Exception
            {
                mockMvc.perform(get("/admin/users/abc")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }


            @Test
            void shouldThrowNotFoundWhenUserDoesNotExist() throws Exception
            {
                mockMvc.perform(get("/admin/users/9999999")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isNotFound())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class ResetUserPassword {
            @Test
            void shouldReturnUserById() throws Exception
            {
                mockMvc.perform(patch("/admin/users/1")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isOk())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").exists())
                       .andExpect(jsonPath("$.data.id").exists());
            }

            @Test
            void shouldThrowUnauthorizedTokenIsMissing() throws Exception
            {
                mockMvc.perform(patch("/admin/users/1"))
                       .andExpect(status().isUnauthorized())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenIdIsZero() throws Exception
            {
                mockMvc.perform(patch("/admin/users/0")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenIdIsInvalid() throws Exception
            {
                mockMvc.perform(patch("/admin/users/abc")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }


            @Test
            void shouldThrowNotFoundWhenUserDoesNotExist() throws Exception
            {
                mockMvc.perform(patch("/admin/users/9999999")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isNotFound())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }
        }
    }

    @Nested
    class UserTest {
        @Nested
        class GetMyProfileTest {
            @Test
            void shouldGetMyProfile() throws Exception
            {
                mockMvc.perform(get("/users/me/1")
                               .header("X-Gateway-Token", token))
                       .andExpect(status().isOk())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").exists());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                mockMvc.perform(get("/users/me/1"))
                       .andExpect(status().isUnauthorized())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }
        }

        @Nested
        class UpdateProfileTest {
            @Test
            void shouldUpdateMyProfile() throws Exception
            {
                UpdateUserRequestDto request = new UpdateUserRequestDto(
                        "updated.john@test.com", "newpassword123", "John Updated"
                );

                mockMvc.perform(put("/users/me/1")
                               .header("X-Gateway-Token", token)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request)))
                       .andExpect(status().isOk())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").exists());
            }

            @Test
            void shouldThrowUnauthorizedWhenTokenIsMissing() throws Exception
            {
                UpdateUserRequestDto request = new UpdateUserRequestDto(
                        "updated@test.com", "newpassword123", "Updated"
                );

                mockMvc.perform(put("/users/me/1")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request)))
                       .andExpect(status().isUnauthorized())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenBodyInvalid() throws Exception
            {
                UpdateUserRequestDto request = new UpdateUserRequestDto(
                        "invalid-email", "short", ""
                );

                mockMvc.perform(put("/users/me/1")
                               .header("X-Gateway-Token", token)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request)))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }

            @Test
            void shouldThrowBadRequestWhenEmailAlreadyBeenTaken() throws Exception
            {
                UpdateUserRequestDto request = new UpdateUserRequestDto(
                        "admin@test.com", "newpassword123", "John"
                );

                mockMvc.perform(put("/users/me/2")
                               .header("X-Gateway-Token", token)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request)))
                       .andExpect(status().isBadRequest())
                       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andExpect(jsonPath("$.data").doesNotExist());
            }
        }
    }
}
