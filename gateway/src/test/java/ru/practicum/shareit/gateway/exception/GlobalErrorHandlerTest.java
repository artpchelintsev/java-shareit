package ru.practicum.shareit.gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalErrorHandlerTest {

    private final GlobalErrorHandler handler = new GlobalErrorHandler();

    @Test
    void handleValidationException_shouldReturnBadRequest() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

        // When
        ResponseEntity<GlobalErrorHandler.ErrorResponse> response = handler.handleValidationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("field: default message", response.getBody().error());
    }

    @Test
    void handleValidationException_withMultipleErrors_shouldReturnFirstError() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "field1", "first error");
        FieldError fieldError2 = new FieldError("object", "field2", "second error");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

        // When
        ResponseEntity<GlobalErrorHandler.ErrorResponse> response = handler.handleValidationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("field1: first error", response.getBody().error());
    }

    @Test
    void handleValidationException_withEmptyErrors_shouldReturnGenericMessage() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

        // When
        ResponseEntity<GlobalErrorHandler.ErrorResponse> response = handler.handleValidationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().error());
    }

    @Test
    void handleWebClientException_shouldReturnSameStatus() {
        // Given
        WebClientResponseException ex = WebClientResponseException.create(
                404, "Not Found", null, null, null);

        // When
        ResponseEntity<Object> response = handler.handleWebClientException(ex);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof GlobalErrorHandler.ErrorResponse);
    }

    @Test
    void handleWebClientException_withServerError_shouldReturnServerError() {
        // Given
        WebClientResponseException ex = WebClientResponseException.create(
                500, "Internal Server Error", null, null, null);

        // When
        ResponseEntity<Object> response = handler.handleWebClientException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleException_shouldReturnInternalServerError() {
        // Given
        Exception ex = new RuntimeException("Test error");

        // When
        ResponseEntity<GlobalErrorHandler.ErrorResponse> response = handler.handleException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().error());
    }

    @Test
    void handleValidationException_custom_shouldReturnBadRequest() {
        // Given
        ValidationException ex = new ValidationException("Custom validation error");

        // When
        ResponseEntity<GlobalErrorHandler.ErrorResponse> response = handler.handleValidationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom validation error", response.getBody().error());
    }

    @Test
    void errorResponse_shouldHaveCorrectStructure() {
        // Given
        GlobalErrorHandler.ErrorResponse errorResponse = new GlobalErrorHandler.ErrorResponse("Test error");

        // Then
        assertEquals("Test error", errorResponse.error()); // проверка поля record
        assertEquals("Test error", errorResponse.getError()); // проверка геттера
    }

    @Test
    void errorResponse_toString() {
        // Given
        GlobalErrorHandler.ErrorResponse errorResponse = new GlobalErrorHandler.ErrorResponse("Test error");

        // When
        String toString = errorResponse.toString();

        // Then
        assertTrue(toString.contains("Test error"));
    }

    @Test
    void errorResponse_equalsAndHashCode() {
        // Given
        GlobalErrorHandler.ErrorResponse response1 = new GlobalErrorHandler.ErrorResponse("error");
        GlobalErrorHandler.ErrorResponse response2 = new GlobalErrorHandler.ErrorResponse("error");
        GlobalErrorHandler.ErrorResponse response3 = new GlobalErrorHandler.ErrorResponse("different");

        // Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}