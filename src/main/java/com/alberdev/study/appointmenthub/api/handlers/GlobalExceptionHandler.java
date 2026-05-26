package com.alberdev.study.appointmenthub.api.handlers;

import com.alberdev.study.appointmenthub.api.handlers.fielderror.ApiError;
import com.alberdev.study.appointmenthub.api.handlers.validation.FieldMessage;
import com.alberdev.study.appointmenthub.api.handlers.validation.ValidationError;
import com.alberdev.study.appointmenthub.domain.exceptions.BusinessException;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                    HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
                                                                        HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                HttpServletRequest request) {
        List<FieldMessage> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        String message = errors.stream()
                .findFirst()
                .map(FieldMessage::message)
                .orElse("Dados invalidos.");

        ValidationError error = new ValidationError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }
}
