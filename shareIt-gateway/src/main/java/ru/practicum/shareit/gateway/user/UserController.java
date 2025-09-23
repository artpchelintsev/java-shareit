package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final ShareItClient shareItClient;

    @PostMapping
    public Mono<ResponseEntity<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return shareItClient.post("/users", userDto, UserDto.class, null)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    @PatchMapping("/{userId}")
    public Mono<ResponseEntity<UserDto>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        log.info("Updating user {} with data: {}", userId, userDto);
        return shareItClient.patch("/users/" + userId, userDto, UserDto.class, null)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable Long userId) {
        log.info("Getting user by id: {}", userId);
        return shareItClient.get("/users/" + userId, UserDto.class, null)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting user: {}", error.getMessage()));
    }

    @GetMapping
    public Mono<ResponseEntity<List<UserDto>>> getAllUsers() {
        log.info("Getting all users");
        ParameterizedTypeReference<List<UserDto>> typeReference =
                new ParameterizedTypeReference<List<UserDto>>() {
                };
        return shareItClient.get("/users", typeReference, null)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting all users: {}", error.getMessage()));
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Object>> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user: {}", userId);
        return shareItClient.delete("/users/" + userId, null)
                .then(Mono.just(ResponseEntity.ok().build()))
                .doOnError(error -> log.error("Error deleting user: {}", error.getMessage()));
    }
}