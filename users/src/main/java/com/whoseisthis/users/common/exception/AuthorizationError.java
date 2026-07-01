package com.whoseisthis.users.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationError extends AppError {
    public AuthorizationError() {
        super("Ooops, you don't have permission.", HttpStatus.FORBIDDEN);
    }
}
