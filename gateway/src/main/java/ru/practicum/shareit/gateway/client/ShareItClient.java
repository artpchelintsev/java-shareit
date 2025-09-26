package ru.practicum.shareit.gateway.client;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ShareItClient {
    private WebClient webClient;

    @Value("${shareit-server.url}")
    private String baseUrl;

    public ShareItClient() {
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public ShareItClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public ShareItClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public <T> Mono<T> post(String path, Object body, Class<T> responseType, Long userId) {
        return webClient.post()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .bodyValue(body != null ? body : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> post(String path, Object body, ParameterizedTypeReference<T> responseType, Long userId) {
        return webClient.post()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .bodyValue(body != null ? body : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> get(String path, Class<T> responseType, Long userId) {
        return webClient.get()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> get(String path, ParameterizedTypeReference<T> responseType, Long userId) {
        return webClient.get()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> patch(String path, Object body, Class<T> responseType, Long userId) {
        return webClient.patch()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .bodyValue(body != null ? body : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> patch(String path, Object body, ParameterizedTypeReference<T> responseType, Long userId) {
        return webClient.patch()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .bodyValue(body != null ? body : "")
                .retrieve()
                .bodyToMono(responseType);
    }

    public Mono<Void> delete(String path, Long userId) {
        return webClient.delete()
                .uri(path)
                .header("X-Sharer-User-Id", userId != null ? userId.toString() : "")
                .retrieve()
                .bodyToMono(Void.class);
    }
}