package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emailToUserId = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public User save(User user) {
        if (emailToUserId.containsKey(user.getEmail()) &&
                (user.getId() == null || !emailToUserId.get(user.getEmail()).equals(user.getId()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (user.getId() == null) {
            user.setId(idCounter.getAndIncrement());
        }

        users.put(user.getId(), user);
        emailToUserId.put(user.getEmail(), user.getId());
        return user;
    }

    public User update(Long userId, User userUpdates) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
            if (emailToUserId.containsKey(userUpdates.getEmail()) &&
                    !emailToUserId.get(userUpdates.getEmail()).equals(userId)) {
                throw new IllegalArgumentException("Email already exists");
            }

            emailToUserId.remove(existingUser.getEmail());
            emailToUserId.put(userUpdates.getEmail(), userId);
            existingUser.setEmail(userUpdates.getEmail());
        }

        if (userUpdates.getName() != null) {
            existingUser.setName(userUpdates.getName());
        }

        users.put(userId, existingUser);
        return existingUser;
    }

    public User findById(Long userId) {
        return users.get(userId);
    }

    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    public void delete(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            emailToUserId.remove(user.getEmail());
            users.remove(userId);
        }
    }

    public User findByEmail(String email) {
        Long userId = emailToUserId.get(email);
        return userId != null ? users.get(userId) : null;
    }
}