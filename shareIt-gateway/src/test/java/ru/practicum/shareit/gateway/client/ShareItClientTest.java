package ru.practicum.shareit.gateway.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShareItClientTest {

    private MockWebServer mockWebServer;
    private ShareItClient shareItClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        shareItClient = new ShareItClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void get_shouldReturnUser() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@email.com\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        // When
        Mono<UserDto> result = shareItClient.get("/users/1", UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(1L, user.getId());
                    assertEquals("Test User", user.getName());
                    assertEquals("test@email.com", user.getEmail());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void get_shouldReturnListWithParameterizedType() {
        // Given
        String responseBody = "[{\"id\":1,\"name\":\"User1\",\"email\":\"user1@email.com\"},{\"id\":2,\"name\":\"User2\",\"email\":\"user2@email.com\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        // When
        ParameterizedTypeReference<List<UserDto>> typeReference =
                new ParameterizedTypeReference<List<UserDto>>() {
                };
        Mono<List<UserDto>> result = shareItClient.get("/users", typeReference, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(users -> {
                    assertEquals(2, users.size());
                    assertEquals("User1", users.get(0).getName());
                    assertEquals("User2", users.get(1).getName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void post_shouldCreateUser() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"New User\",\"email\":\"new@email.com\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        UserDto newUser = new UserDto(null, "New User", "new@email.com");

        // When
        Mono<UserDto> result = shareItClient.post("/users", newUser, UserDto.class, null);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(1L, user.getId());
                    assertEquals("New User", user.getName());
                    assertEquals("new@email.com", user.getEmail());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void patch_shouldUpdateUser() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"Updated User\",\"email\":\"test@email.com\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        UserDto updateUser = new UserDto(null, "Updated User", null);

        // When
        Mono<UserDto> result = shareItClient.patch("/users/1", updateUser, UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertEquals(1L, user.getId());
                    assertEquals("Updated User", user.getName());
                    assertEquals("test@email.com", user.getEmail());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void delete_shouldDeleteUser() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        Mono<Void> result = shareItClient.delete("/users/1", 1L);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void get_shouldHandleError() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        // When
        Mono<UserDto> result = shareItClient.get("/users/999", UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }
}