package ru.practicum.shareit.server.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException_shouldReturn404WithMessage() {
        NotFoundException ex = new NotFoundException("Not found");

        ErrorResponse response = errorHandler.handleNotFoundException(ex);

        assertThat(response.error()).isEqualTo("Not found");
    }

    @Test
    void handleConflictException_shouldReturn409WithMessage() {
        ConflictException ex = new ConflictException("Conflict occurred");

        ErrorResponse response = errorHandler.handleConflictException(ex);

        assertThat(response.error()).isEqualTo("Conflict occurred");
    }

    @Test
    void handleIllegalArgumentException_shouldReturn400WithMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ErrorResponse response = errorHandler.handleIllegalArgumentException(ex);

        assertThat(response.error()).isEqualTo("Invalid argument");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturn400WithFieldMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "must not be blank"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(ex);

        assertThat(response.error()).contains("Validation failed: must not be blank");
    }

    @Test
    void handleConstraintViolationException_shouldReturn400WithViolationMessage() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must be positive");

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(violation));

        ErrorResponse response = errorHandler.handleConstraintViolationException(ex);

        assertThat(response.error()).contains("Validation failed: must be positive");
    }

    @Test
    void handleForbiddenException_shouldReturn403WithMessage() {
        ForbiddenException ex = new ForbiddenException("Access denied");

        ErrorResponse response = errorHandler.handleForbiddenException(ex);

        assertThat(response.error()).isEqualTo("Access denied");
    }

    @Test
    void handleLazyInitializationException_shouldReturn500WithMessage() {
        LazyInitializationException ex = new LazyInitializationException("failed to fetch");

        ErrorResponse response = errorHandler.handleLazyInitializationException(ex);

        assertThat(response.error()).contains("Lazy loading error: failed to fetch");
    }

    @Test
    void handleDataAccessException_shouldReturn500WithMessage() {
        DataAccessException ex = new DataAccessException("DB error") {
        };

        ErrorResponse response = errorHandler.handleDataAccessException(ex);

        assertThat(response.error()).contains("Database access error: ");
    }

    @Test
    void handleGenericException_shouldReturn500InternalServerError() {
        Exception ex = new Exception("Unexpected");

        ErrorResponse response = errorHandler.handleException(ex);

        assertThat(response.error()).isEqualTo("Internal server error");
    }
}
