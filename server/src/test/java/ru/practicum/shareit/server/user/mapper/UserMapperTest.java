package ru.practicum.shareit.server.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_shouldConvertUserToDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        // When
        UserDto userDto = UserMapper.toUserDto(user);

        // Then
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("Test User", userDto.getName());
        assertEquals("test@email.com", userDto.getEmail());
    }

    @Test
    void toUser_shouldConvertDtoToUser() {
        // Given
        UserDto userDto = new UserDto(1L, "Test User", "test@email.com");

        // When
        User user = UserMapper.toUser(userDto);

        // Then
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    void toUser_shouldHandleNullDto() {
        // When
        User user = UserMapper.toUser(null);

        // Then
        assertNull(user);
    }

    @Test
    void toUserDto_shouldHandleNullUser() {
        // When
        UserDto userDto = UserMapper.toUserDto(null);

        // Then
        assertNull(userDto);
    }
}