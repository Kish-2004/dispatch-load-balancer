package com.logistics.dispatch.exception;


import com.logistics.dispatch.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleValidationErrors() {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "orders",
                        "must not be empty"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse> response =
                exceptionHandler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("orders: must not be empty",
                response.getBody().message());
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleConstraintViolation() {

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        "Invalid field value",
                        Set.of()
                );

        ResponseEntity<ApiResponse> response =
                exceptionHandler.handleConstraintViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid field value",
                response.getBody().message());
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleInvalidJson() {

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Invalid JSON",
                        (HttpInputMessage) null
                );

        ResponseEntity<ApiResponse> response =
                exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Invalid request body or unsupported enum value.",
                response.getBody().message()
        );
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleDatabaseConflict() {

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Duplicate ID");

        ResponseEntity<ApiResponse> response =
                exceptionHandler.handleDatabaseConflict(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "A database constraint was violated. Check duplicate IDs and input values.",
                response.getBody().message()
        );
        assertEquals("error", response.getBody().status());
    }

    @Test
    void shouldHandleUnexpectedException() {

        Exception exception =
                new RuntimeException("Internal error");

        ResponseEntity<ApiResponse> response =
                exceptionHandler.handleGeneralException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
        assertEquals(
                "An unexpected internal server error occurred.",
                response.getBody().message()
        );
        assertEquals("error", response.getBody().status());
    }
}
