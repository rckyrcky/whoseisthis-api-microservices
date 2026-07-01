package com.whoseisthis.users.common.exception;

import org.springframework.http.HttpStatus;

public class RateLimitError extends AppError {
    public RateLimitError() {
        super("Too many requests. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
    }
}
