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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void constructor_withBaseUrl_shouldCreateClient() {
        // Given
        String baseUrl = "http://localhost:8080";

        // When
        ShareItClient client = new ShareItClient(baseUrl);

        // Then - should not throw exception
        assertNotNull(client);
    }

    @Test
    void constructor_withWebClient_shouldCreateClient() {
        // Given
        WebClient webClient = WebClient.create();

        // When
        ShareItClient client = new ShareItClient(webClient);

        // Then
        assertNotNull(client);
    }

    @Test
    void post_withParameterizedType_shouldWork() {
        // Given
        String responseBody = "[{\"id\":1,\"name\":\"User1\"},{\"id\":2,\"name\":\"User2\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        UserDto newUser = new UserDto(null, "New User", "new@email.com");
        ParameterizedTypeReference<List<UserDto>> typeReference =
                new ParameterizedTypeReference<List<UserDto>>() {
                };

        // When
        Mono<List<UserDto>> result = shareItClient.post("/users", newUser, typeReference, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(users -> {
                    assertEquals(2, users.size());
                    assertEquals("User1", users.get(0).getName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void post_withNullBody_shouldWork() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"User\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        // When
        Mono<UserDto> result = shareItClient.post("/users", null, UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void post_withNullUserId_shouldWork() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"User\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        UserDto newUser = new UserDto(null, "User", "user@email.com");

        // When
        Mono<UserDto> result = shareItClient.post("/users", newUser, UserDto.class, null);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void patch_withParameterizedType_shouldWork() {
        // Given
        String responseBody = "[{\"id\":1,\"name\":\"Updated1\"},{\"id\":2,\"name\":\"Updated2\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        UserDto updateUser = new UserDto(null, "Updated", null);
        ParameterizedTypeReference<List<UserDto>> typeReference =
                new ParameterizedTypeReference<List<UserDto>>() {
                };

        // When
        Mono<List<UserDto>> result = shareItClient.patch("/users", updateUser, typeReference, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(users -> {
                    assertEquals(2, users.size());
                    assertEquals("Updated1", users.get(0).getName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void patch_withNullBody_shouldWork() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"User\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        // When
        Mono<UserDto> result = shareItClient.patch("/users/1", null, UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void get_withNullUserId_shouldWork() {
        // Given
        String responseBody = "{\"id\":1,\"name\":\"User\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        // When
        Mono<UserDto> result = shareItClient.get("/users/1", UserDto.class, null);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void delete_withNullUserId_shouldWork() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        Mono<Void> result = shareItClient.delete("/users/1", null);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void post_shouldHandleServerError() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        UserDto newUser = new UserDto(null, "User", "user@email.com");

        // When
        Mono<UserDto> result = shareItClient.post("/users", newUser, UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    void patch_shouldHandleClientError() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad Request"));

        UserDto updateUser = new UserDto(null, "User", "user@email.com");

        // When
        Mono<UserDto> result = shareItClient.patch("/users/1", updateUser, UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    void get_shouldHandleNetworkError() {
        // Given - server will be shut down
        try {
            tearDown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // When
        Mono<UserDto> result = shareItClient.get("/users/1", UserDto.class, 1L);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }
}