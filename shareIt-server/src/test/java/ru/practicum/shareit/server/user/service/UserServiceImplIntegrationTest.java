package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.ShareItServerApp;
import ru.practicum.shareit.server.exception.ConflictException;
import ru.practicum.shareit.server.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServerApp.class)
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        // Given
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");

        // When
        UserDto createdUser = userService.createUser(userDto);

        // Then
        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("test@email.com", createdUser.getEmail());
    }

    @Test
    void createUser_shouldThrowConflictExceptionWhenEmailExists() {
        // Given
        UserDto userDto1 = new UserDto(null, "User1", "same@email.com");
        UserDto userDto2 = new UserDto(null, "User2", "same@email.com");

        // When
        userService.createUser(userDto1);

        // Then
        assertThrows(ConflictException.class, () -> userService.createUser(userDto2));
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        // Given
        UserDto userDto = new UserDto(null, "Original", "original@email.com");
        UserDto createdUser = userService.createUser(userDto);
        UserDto updateDto = new UserDto(null, "Updated", null);

        // When
        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        // Then
        assertEquals("Updated", updatedUser.getName());
        assertEquals("original@email.com", updatedUser.getEmail());
    }
}