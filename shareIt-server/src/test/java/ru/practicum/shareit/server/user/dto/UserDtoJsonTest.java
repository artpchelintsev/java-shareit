package ru.practicum.shareit.server.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialize() throws Exception {
        // Given
        UserDto userDto = new UserDto(1L, "Test User", "test@email.com");

        // When
        String result = objectMapper.writeValueAsString(userDto);

        // Then
        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"name\":\"Test User\"");
        assertThat(result).contains("\"email\":\"test@email.com\"");
    }

    @Test
    void testDeserialize() throws Exception {
        // Given
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        // When
        UserDto result = objectMapper.readValue(content, UserDto.class);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void shouldValidateValidUserDto() {
        // Given
        UserDto userDto = new UserDto(1L, "Valid Name", "valid@email.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        // Given
        UserDto userDto = new UserDto(1L, "Valid Name", "invalid-email");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Email should be valid");
    }
}