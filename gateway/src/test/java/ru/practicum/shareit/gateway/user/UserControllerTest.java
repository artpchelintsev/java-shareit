package ru.practicum.shareit.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = UserController.class)
@Import(ShareItClient.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShareItClient shareItClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        // Given
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        UserDto createdUser = new UserDto(1L, "Test User", "test@email.com");

        when(shareItClient.post(eq("/users"), any(UserDto.class), eq(UserDto.class), isNull()))
                .thenReturn(Mono.just(createdUser));

        // When & Then
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test User")
                .jsonPath("$.email").isEqualTo("test@email.com");

        verify(shareItClient, times(1)).post(eq("/users"), any(UserDto.class), eq(UserDto.class), isNull());
    }

    @Test
    void getUserById_shouldReturnUser() {
        // Given
        UserDto userDto = new UserDto(1L, "Test User", "test@email.com");

        when(shareItClient.get(eq("/users/1"), eq(UserDto.class), isNull()))
                .thenReturn(Mono.just(userDto));

        // When & Then
        webTestClient.get()
                .uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test User");

        verify(shareItClient, times(1)).get(eq("/users/1"), eq(UserDto.class), isNull());
    }

    @Test
    void getAllUsers_shouldReturnUsers() {
        // Given
        List<UserDto> users = List.of(
                new UserDto(1L, "User1", "user1@email.com"),
                new UserDto(2L, "User2", "user2@email.com")
        );

        when(shareItClient.get(eq("/users"), any(ParameterizedTypeReference.class), isNull()))
                .thenReturn(Mono.just(users));

        // When & Then
        webTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .hasSize(2)
                .contains(users.get(0), users.get(1));

        verify(shareItClient, times(1)).get(eq("/users"), any(ParameterizedTypeReference.class), isNull());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // Given
        UserDto updateDto = new UserDto(null, "Updated User", null);
        UserDto updatedUser = new UserDto(1L, "Updated User", "test@email.com");

        when(shareItClient.patch(eq("/users/1"), any(UserDto.class), eq(UserDto.class), isNull()))
                .thenReturn(Mono.just(updatedUser));

        // When & Then
        webTestClient.patch()
                .uri("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated User");

        verify(shareItClient, times(1)).patch(eq("/users/1"), any(UserDto.class), eq(UserDto.class), isNull());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        // Given
        when(shareItClient.delete(eq("/users/1"), isNull()))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/users/1")
                .exchange()
                .expectStatus().isOk();

        verify(shareItClient, times(1)).delete(eq("/users/1"), isNull());
    }

    @Test
    void createUser_shouldReturnBadRequestWhenInvalidData() {
        // Given
        UserDto invalidUser = new UserDto(null, "", "invalid-email");

        // When & Then - Spring Validation should catch this before reaching ShareItClient
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest();

        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(Class.class), any());
        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(ParameterizedTypeReference.class), any());
    }

    @Test
    void createUser_shouldReturnBadRequestWhenEmailIsNull() {
        // Given
        UserDto invalidUser = new UserDto(null, "Test User", null);

        // When & Then
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest();

        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(Class.class), any());
        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(ParameterizedTypeReference.class), any());
    }

    @Test
    void createUser_shouldReturnBadRequestWhenNameIsNull() {
        // Given
        UserDto invalidUser = new UserDto(null, null, "test@email.com");

        // When & Then
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUser)
                .exchange()
                .expectStatus().isBadRequest();

        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(Class.class), any());
        verify(shareItClient, never()).post(anyString(), any(UserDto.class), any(ParameterizedTypeReference.class), any());
    }
}