package com.whoseisthis.gateway.auth.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDto(Long id, String token) {
    public LoginResponseDto(Long id) {
        this(id, null);
    }
}
