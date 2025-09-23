package ru.practicum.shareit.gateway.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValidationExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        // Given
        String message = "Test validation error";

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withCause_shouldWork() {
        // Given
        String message = "Test validation error";
        Throwable cause = new RuntimeException("Root cause");

        // When
        ValidationException exception = new ValidationException(message);
        exception.initCause(cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}