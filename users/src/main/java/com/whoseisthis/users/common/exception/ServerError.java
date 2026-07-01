package com.whoseisthis.users.common.exception;

import org.springframework.http.HttpStatus;

public class ServerError extends AppError {
    public ServerError() {
        super("Server is unavailable now.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
