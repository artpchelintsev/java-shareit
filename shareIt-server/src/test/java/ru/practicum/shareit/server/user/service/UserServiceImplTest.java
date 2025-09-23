package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        // Given
        User user = new User(1L, "Test User", "test@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Given
        User user1 = new User(1L, "User 1", "user1@email.com");
        User user2 = new User(2L, "User 2", "user2@email.com");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }
}