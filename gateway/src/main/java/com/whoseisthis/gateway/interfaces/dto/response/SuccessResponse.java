package com.whoseisthis.gateway.interfaces.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessResponse<T>(String message, T data) {
    public SuccessResponse(String message)
    {
        this(message, null);
    }
}
