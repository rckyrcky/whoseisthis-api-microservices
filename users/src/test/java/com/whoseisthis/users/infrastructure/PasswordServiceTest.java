package com.whoseisthis.users.infrastructure;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    @Nested
    class HashTest{
        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldHashPassword(){
            // Arrange
            String password = "new-password";
            String hashed = "hashed-password";
            when(passwordEncoder.encode(password)).thenReturn(hashed);

            // Action
            var result = passwordService.hash(password);

            // Assert
            assertEquals(hashed, result);
        }
    }

    @Nested
    class CompareTest{
        @Test
        void shouldComparePassword(){
            // Arrange
            String password = "new-password";
            String hashed = "hashed-password";
            when(passwordEncoder.matches(password, hashed)).thenReturn(true);

            // Action
            var result = passwordService.compare(password, hashed);

            // Assert
            assertTrue(result);
        }
    }
}
