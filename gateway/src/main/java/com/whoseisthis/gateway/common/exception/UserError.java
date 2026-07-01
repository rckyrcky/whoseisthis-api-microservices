package com.whoseisthis.gateway.common.exception;

import org.springframework.http.HttpStatus;

public class UserError extends AppError {
    public UserError(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public UserError() {
        super("An error occurred. Please retry.", HttpStatus.BAD_REQUEST);
    }
}
