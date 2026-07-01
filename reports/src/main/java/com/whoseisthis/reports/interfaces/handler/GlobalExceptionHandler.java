package com.whoseisthis.reports.interfaces.handler;

import com.whoseisthis.reports.common.exception.AppError;
import com.whoseisthis.reports.common.exception.NotFoundError;
import com.whoseisthis.reports.common.exception.ServerError;
import com.whoseisthis.reports.interfaces.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex)
    {
        var errors = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> Map.of("field",
                fieldError.getField(),
                "message",
                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "unknown")).toList();

        ErrorResponse errorResponse = new ErrorResponse("An error occurred. Please retry.", errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleValidationError(Exception ex)
    {
        ErrorResponse err = new ErrorResponse("Invalid parameter or payload");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handle404(Exception ex)
    {
        ErrorResponse err = new ErrorResponse(new NotFoundError().getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpError(AppError ex, HttpServletRequest request)
    {
        log.error("API error | method={} | uri={} | status={} | ip={} | message={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getStatusCode(),
                request.getRemoteAddr(),
                ex.getMessage(),
                ex);
        ErrorResponse err = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(err);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception ex)
    {
        log.error("Unknown error | message={}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(new ServerError().getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
