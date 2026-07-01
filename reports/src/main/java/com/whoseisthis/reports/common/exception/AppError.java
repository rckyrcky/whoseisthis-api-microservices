package com.whoseisthis.reports.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppError extends RuntimeException {
    private final HttpStatus statusCode;

    public AppError(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}