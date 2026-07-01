package com.whoseisthis.users.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationError extends AppError {
    public AuthenticationError() {
        super("You must login first.", HttpStatus.UNAUTHORIZED);
    }
}
