package ru.practicum.shareit.server.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUserWhenEmailExists() {
        // Given
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByEmail("test@email.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@email.com", found.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenEmailNotExists() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@email.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrueWhenEmailExistsForDifferentUser() {
        // Given
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("test@email.com");
        entityManager.persist(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("another@email.com");
        entityManager.persist(user2);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("test@email.com", user2.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        User user = new User();
        user.setName("User");
        user.setEmail("test@email.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@email.com", user.getId());

        // Then
        assertFalse(exists);
    }
}