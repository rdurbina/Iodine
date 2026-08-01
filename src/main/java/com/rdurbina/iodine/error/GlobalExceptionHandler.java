package com.rdurbina.iodine.error;

import com.rdurbina.iodine.error.constant.ErrorMessages;
import com.rdurbina.iodine.error.constant.ErrorType;
import com.rdurbina.iodine.error.response.ErrorDetail;
import com.rdurbina.iodine.error.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ErrorDetail> details = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toErrorDetail)
                .distinct()
                .toList();

        return response(
                HttpStatus.BAD_REQUEST,
                ErrorType.VALIDATION,
                ErrorMessages.VALIDATION,
                request,
                details
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.CONFLICT,
                ErrorType.VALIDATION,
                exception.getMessage(),
                request,
                exception.getDetails()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                ErrorType.NOT_FOUND,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                ErrorType.AUTHENTICATION,
                ErrorMessages.BAD_CREDENTIALS,
                request,
                List.of()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                ErrorType.VALIDATION,
                ErrorMessages.INVALID_REQUEST,
                request,
                List.of()
        );
    }

    private ErrorDetail toErrorDetail(FieldError fieldError) {
        return new ErrorDetail(formatFieldName(fieldError.getField()), fieldError.getDefaultMessage());
    }

    private String formatFieldName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            ErrorType errorType,
            String message,
            HttpServletRequest request,
            List<ErrorDetail> details
    ) {
        ErrorResponse response = new ErrorResponse(
                errorType.getType(),
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
