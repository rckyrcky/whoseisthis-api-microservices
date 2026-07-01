package com.whoseisthis.users.interfaces.dto;
import jakarta.validation.constraints.Size;


public record UserFilter(
        @Size(max = 20, message = "Query parameter must not exceed 20 characters")
        String name,
        @Size(max = 20, message = "Query parameter must not exceed 20 characters")
        String email
) {
}
