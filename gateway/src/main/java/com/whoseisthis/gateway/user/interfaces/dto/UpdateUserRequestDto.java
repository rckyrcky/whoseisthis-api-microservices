package com.whoseisthis.gateway.user.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
        @Email(message = "Invalid email")
        String email,

        @Size(min = 8, max = 30, message = "Password must be between 8–30 characters")
        String password,

        @Size(min = 1, max = 100, message = "Name must not exceed 100 characters")
        String name
) {
}
