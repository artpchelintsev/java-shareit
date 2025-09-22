package ru.practicum.shareit.gateway.user.dto;

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
        UserDto userDto = new UserDto(1L, "Gateway User", "gateway@email.com");

        // When
        String result = objectMapper.writeValueAsString(userDto);

        // Then
        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"name\":\"Gateway User\"");
        assertThat(result).contains("\"email\":\"gateway@email.com\"");
    }

    @Test
    void testDeserialize() throws Exception {
        // Given
        String content = "{\"id\":1,\"name\":\"Gateway User\",\"email\":\"gateway@email.com\"}";

        // When
        UserDto result = objectMapper.readValue(content, UserDto.class);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Gateway User");
        assertThat(result.getEmail()).isEqualTo("gateway@email.com");
    }

    @Test
    void shouldValidateGatewayUserDto() {
        // Given
        UserDto userDto = new UserDto(1L, "Valid Gateway User", "valid@email.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertThat(violations).isEmpty();
    }
}