//package com.jaswanth.fooddeliveryplatform.common.exception.global;
//
//import com.jaswanth.fooddeliveryplatform.common.dto.ApiErrorResponse;
//import com.jaswanth.fooddeliveryplatform.common.dto.ErrorResponse;
//import com.jaswanth.fooddeliveryplatform.common.dto.ValidationErrorResponse;
//import com.jaswanth.fooddeliveryplatform.common.exception.auth.*;
//import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
//import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
//import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.NoSuchElementException;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(DuplicateEmailException.class)
//    public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(
//            DuplicateEmailException exception
//    ){
//        ApiErrorResponse response = new ApiErrorResponse(
//                HttpStatus.CONFLICT.value(),
//                exception.getMessage(),
//                LocalDateTime.now()
//
//        );
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
//            MethodArgumentNotValidException exception
//    ){
//        Map<String, String> errors = new HashMap<>();
//
//        exception.getBindingResult()
//                .getFieldErrors()
//                .forEach(error ->
//                        errors.put(
//                                error.getField(),
//                                error.getDefaultMessage()
//                        )
//                );
//        ValidationErrorResponse response = new ValidationErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                "Validation failed",
//                errors,
//                LocalDateTime.now()
//        );
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(response);
//
//    }
//
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
//            UserNotFoundException exception
//    ){
//        ApiErrorResponse response = new ApiErrorResponse(
//                HttpStatus.NOT_FOUND.value(),
//                exception.getMessage(),
//                LocalDateTime.now()
//        );
//
//        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }
//
//    @ExceptionHandler(InvalidRoleRequestException.class)
//    public ResponseEntity<ApiErrorResponse> handleInvalidRoleRequest(
//            InvalidRoleRequestException exception
//    ){
//        ApiErrorResponse response = new ApiErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                exception.getMessage(),
//                LocalDateTime.now()
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    // 1. Validation Failures (@Valid, @Pattern, @Email, @ValidPhoneNumber)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationExceptions(
//            MethodArgumentNotValidException ex, HttpServletRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
//            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
//        }
//
//        ErrorResponse response = ErrorResponse.withValidation(
//                HttpStatus.BAD_REQUEST.value(),
//                "VALIDATION_FAILED",
//                "Input payload validation failed",
//                request.getRequestURI(),
//                errors
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    // 2. Email Verification Required
//    @ExceptionHandler(EmailNotVerifiedException.class)
//    public ResponseEntity<ErrorResponse> handleEmailNotVerified(
//            EmailNotVerifiedException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.FORBIDDEN.value(),
//                "EMAIL_NOT_VERIFIED",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }
//
//    // 3. Invalid Phone Number
//    @ExceptionHandler(InvalidPhoneNumberException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidPhone(
//            InvalidPhoneNumberException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.BAD_REQUEST.value(),
//                "INVALID_PHONE_NUMBER",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    @ExceptionHandler(InvalidCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
//            InvalidCredentialsException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.UNAUTHORIZED.value(),
//                "INVALID_CREDENTIALS",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//    }
//
//    // 4. Resource Not Found (404)
//    @ExceptionHandler({ResourceNotFoundException.class, NoSuchElementException.class})
//    public ResponseEntity<ErrorResponse> handleNotFound(
//            RuntimeException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.NOT_FOUND.value(),
//                "RESOURCE_NOT_FOUND",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }
//
//    // 5. Business Conflict (409)
//    @ExceptionHandler(BusinessConflictException.class)
//    public ResponseEntity<ErrorResponse> handleConflict(
//            BusinessConflictException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.CONFLICT.value(),
//                "RESOURCE_CONFLICT",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//    }
//
//    // 6. State Machine & Argument Exceptions (400)
//    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
//    public ResponseEntity<ErrorResponse> handleBadRequest(
//            RuntimeException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.BAD_REQUEST.value(),
//                "BAD_REQUEST",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    // 7. Security Access Denied (403)
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDenied(
//            AccessDeniedException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.FORBIDDEN.value(),
//                "ACCESS_DENIED",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }
//
//    // 8. General Catch-All (500)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleInternalServerError(
//            Exception ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "INTERNAL_SERVER_ERROR",
//                "An unexpected internal server error occurred",
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//
//    @ExceptionHandler(DuplicatePhoneNumberException.class)
//    public ResponseEntity<ErrorResponse> handleDuplicatePhone(
//            DuplicatePhoneNumberException ex, HttpServletRequest request) {
//        ErrorResponse response = ErrorResponse.of(
//                HttpStatus.CONFLICT.value(),
//                "PHONE_ALREADY_EXISTS",
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//    }
//}

package com.jaswanth.fooddeliveryplatform.common.exception.global;

import com.jaswanth.fooddeliveryplatform.common.dto.ErrorResponse;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.*;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validation Failures (@Valid, @Pattern, @Email, @ValidPhoneNumber)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = ErrorResponse.withValidation(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "Input payload validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Auth: Duplicate Email
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "DUPLICATE_EMAIL",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 3. Auth: Duplicate Phone Number
    @ExceptionHandler(DuplicatePhoneNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePhone(
            DuplicatePhoneNumberException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "PHONE_ALREADY_EXISTS",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 4. Auth: Email Verification Required
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerified(
            EmailNotVerifiedException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "EMAIL_NOT_VERIFIED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 5. Auth: Invalid Credentials (401)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_CREDENTIALS",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 6. Validation: Invalid Phone Number
    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPhone(
            InvalidPhoneNumberException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PHONE_NUMBER",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 7. Resource Not Found (404)
    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 8. Business Conflicts (409)
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            BusinessConflictException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "RESOURCE_CONFLICT",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 9. Bad Request & State Transitions (400)
    @ExceptionHandler({
            IllegalStateException.class,
            IllegalArgumentException.class,
            InvalidRoleRequestException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 10. Access Denied (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 11. General Catch-All (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(
            Exception ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal server error occurred",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}