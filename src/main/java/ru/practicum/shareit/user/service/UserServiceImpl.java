package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User user = UserMapper.toUser(userDto);
            User savedUser = userRepository.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Email already exists")) {
                throw new ConflictException("Email already exists");
            }
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        try {
            User existingUser = userRepository.findById(userId);
            if (existingUser == null) {
                throw new NotFoundException("User not found");
            }

            User userUpdates = new User();
            userUpdates.setName(userDto.getName());
            userUpdates.setEmail(userDto.getEmail());

            User updatedUser = userRepository.update(userId, userUpdates);
            return UserMapper.toUserDto(updatedUser);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Email already exists")) {
                throw new ConflictException("Email already exists");
            } else if (e.getMessage().equals("User not found")) {
                throw new NotFoundException("User not found");
            }
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        userRepository.delete(userId);
    }
}