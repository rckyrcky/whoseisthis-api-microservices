package com.whoseisthis.users.infrastructure;

import com.whoseisthis.users.application.JwtPayload;
import com.whoseisthis.users.core.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp()
    {
        jwtService = new JwtService("my-super-long-secret-key-my-super-long-secret-key", "whoseisthis");
    }

    @Nested
    class GenerateTest {
        @Test
        void shouldGenerateJwt()
        {
            // Arrange
            var payload = new JwtPayload(1L, UserRole.USER);

            // Action
            var result = jwtService.generate(payload);

            // Assert
            assertNotNull(result);
        }
    }
}
