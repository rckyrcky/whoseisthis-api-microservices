package com.whoseisthis.users.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email can't be empty")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password can't be empty")
        String password) {
}
