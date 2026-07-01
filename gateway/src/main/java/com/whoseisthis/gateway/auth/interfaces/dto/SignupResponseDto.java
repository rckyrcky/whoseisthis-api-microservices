package com.whoseisthis.gateway.auth.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignupResponseDto(Long id, String token) {
    public SignupResponseDto(Long id) {
        this(id, null);
    }
}
