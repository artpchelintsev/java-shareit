package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void userConstructor_shouldCreateUserWithParameters() {
        // Given & When
        User user = new User(1L, "Test User", "test@email.com");

        // Then
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    void userSetters_shouldSetAllFields() {
        // Given
        User user = new User();

        // When
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        // Then
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    void userEquals_shouldReturnTrueForSameUsers() {
        // Given
        User user1 = new User(1L, "User", "user@email.com");
        User user2 = new User(1L, "User", "user@email.com");

        // When & Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void userEquals_shouldReturnFalseForDifferentUsers() {
        // Given
        User user1 = new User(1L, "User1", "user1@email.com");
        User user2 = new User(2L, "User2", "user2@email.com");

        // When & Then
        assertNotEquals(user1, user2);
    }

    @Test
    void userToString_shouldReturnStringRepresentation() {
        // Given
        User user = new User(1L, "Test User", "test@email.com");

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Test User"));
        assertTrue(toString.contains("test@email.com"));
    }
}