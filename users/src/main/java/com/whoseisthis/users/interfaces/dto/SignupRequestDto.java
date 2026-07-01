package com.whoseisthis.users.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequestDto(
        @NotBlank(message = "Email can't be empty")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password can't be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8–30 characters")
        String password,

        @NotBlank(message = "Name can't be empty")
        @Size(min = 1, max = 100, message = "Name must not exceed 100 characters")
        String name) {
}
