package com.whoseisthis.gateway.interfaces.handler;

import com.whoseisthis.gateway.common.exception.AppError;
import com.whoseisthis.gateway.common.exception.NotFoundError;
import com.whoseisthis.gateway.common.exception.ServerError;
import com.whoseisthis.gateway.interfaces.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

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

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
            ServerWebInputException.class})
    public ResponseEntity<ErrorResponse> handleValidationError(Exception ex)
    {
        ErrorResponse err = new ErrorResponse("Invalid parameter or payload");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handle404(Exception ex)
    {
        ErrorResponse err = new ErrorResponse(new NotFoundError().getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpError(AppError ex, ServerWebExchange exchange)
    {
        log.error("API error | method={} | uri={} | status={} | ip={} | message={}",
                exchange.getRequest().getMethod().name(),
                exchange.getRequest().getURI(),
                ex.getStatusCode(),
                exchange.getRequest().getRemoteAddress() != null ? exchange
                        .getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress() : "unknown",
                ex.getMessage(),
                ex);
        ErrorResponse err = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(err);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<JsonNode> handleApiClientError(WebClientResponseException ex) {
        var body = objectMapper.readTree(ex.getResponseBodyAsString());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(body);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception ex)
    {
        log.error("Unknown error | message={}", ex.getMessage(), ex);
        ErrorResponse err = new ErrorResponse(new ServerError().getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
