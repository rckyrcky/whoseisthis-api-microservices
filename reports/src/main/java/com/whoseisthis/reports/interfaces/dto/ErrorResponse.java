package com.whoseisthis.reports.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, Object errors) {
    public ErrorResponse(String message)
    {
        this(message, null);
    }
}
