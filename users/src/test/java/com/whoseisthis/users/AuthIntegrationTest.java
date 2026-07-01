package com.whoseisthis.users;

import com.whoseisthis.users.infrastructure.repository.UserRepository;
import com.whoseisthis.users.interfaces.dto.LoginRequestDto;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Value("${gateway-token}")
    private String token;

    @Nested
    class SignupTest {
        @Test
        void shouldThrowBadRequestWhenEmailInvalid() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi", "halo12345", "budi");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.data").doesNotExist());
            assertFalse(userRepository.existsByEmail(dto.email()));
        }

        @Test
        void shouldThrowBadRequestWhenPasswordLengthBelowMinimum() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo", "budi");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.data").doesNotExist());
            assertFalse(userRepository.existsByEmail(dto.email()));
        }

        @Test
        void shouldThrowBadRequestWhenPasswordLengthAboveMaximum() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com",
                    "haloooooooooooooooooooooooooooooooooooooooooooooooooooooooo",
                    "budi");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.data").doesNotExist());
            assertFalse(userRepository.existsByEmail(dto.email()));
        }

        @Test
        void shouldThrowBadRequestWhenNameIsEmpty() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "");

            // Assert
            mockMvc.perform(MockMvcRequestBuilders
                           .post("/auth/signup")
                           .header("X-Gateway-Token", token)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.message").exists())
                   .andExpect(jsonPath("$.errors").exists())
                   .andExpect(jsonPath("$.data").doesNotExist());
            assertFalse(userRepository.existsByEmail(dto.email()));
        }

        @Test
        void shouldThrowBadRequestWhenEmailDuplicate() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "");
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/auth/signup")
                    .header("X-Gateway-Token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));

            // Assert
            mockMvc.perform(MockMvcRequestBuilders
                           .post("/auth/signup")
                           .header("X-Gateway-Token", token)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(dto)))
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.message").exists())
                   .andExpect(jsonPath("$.errors").exists())
                   .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void shouldSignup() throws Exception
        {
            // Arrange
            var dto = new SignupRequestDto("budi@test.com", "halo12345", "budi");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/signup")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").exists());
            assertTrue(userRepository.existsByEmail(dto.email()));
        }
    }

    @Nested
    class LoginTest {
        @Test
        void shouldThrowBadRequestWhenEmailInvalid() throws Exception
        {
            // Arrange
            var dto = new LoginRequestDto("budi", "halo12345");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void shouldThrowBadRequestWhenPasswordInvalid() throws Exception
        {
            // Arrange
            var dto = new LoginRequestDto("budi@test.com", "halo");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void shouldLogin() throws Exception
        {
            // Arrange
            var dto = new LoginRequestDto("john@test.com", "halo12345");

            // Assert
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .header("X-Gateway-Token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").exists());
        }
    }
}
