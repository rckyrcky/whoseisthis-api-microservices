package com.whoseisthis.reports.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundError extends AppError {
    public NotFoundError() {
        super("Sorry, we can't find that.", HttpStatus.NOT_FOUND);
    }
}
