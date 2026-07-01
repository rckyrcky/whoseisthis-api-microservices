package com.whoseisthis.users.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDto(Long id, String name, String email) {
    public UserResponseDto(Long id) {
        this(id, null, null);
    }
}
