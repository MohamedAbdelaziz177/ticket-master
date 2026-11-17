package com.ticketmaster.searchservice.result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Keeps every error response inside the project's {@link Result} envelope instead of
 * Spring's default error body.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            HandlerMethodValidationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Result<Void>> handleInvalidRequest(Exception exception) {
        return failure(HttpStatus.BAD_REQUEST, ApiError.Validation(describe(exception)));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoResourceFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, ApiError.NotFound("Resource not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpected(Exception exception) {
        log.error("Unhandled exception while processing request", exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, ApiError.ServerError("Unexpected error"));
    }

    private ResponseEntity<Result<Void>> failure(HttpStatus status, ApiError error) {
        return ResponseEntity.status(status).body(Result.Failure(error));
    }

    private String describe(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException validationException
                && validationException.getBindingResult().getFieldError() != null) {
            String field = validationException.getBindingResult().getFieldError().getField();
            String message = validationException.getBindingResult().getFieldError().getDefaultMessage();
            return field + ": " + message;
        }

        if (exception instanceof BindException bindException
                && bindException.getBindingResult().getFieldError() != null) {
            String field = bindException.getBindingResult().getFieldError().getField();
            String message = bindException.getBindingResult().getFieldError().getDefaultMessage();
            return field + ": " + message;
        }

        return "Invalid request";
    }
}
