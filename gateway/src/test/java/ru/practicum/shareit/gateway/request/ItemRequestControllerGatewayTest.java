package ru.practicum.shareit.gateway.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.util.GatewayConstants;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ItemRequestController.class)
class ItemRequestControllerGatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ShareItClient shareItClient;

    @Test
    void createItemRequest_shouldReturnRequest() {
        ItemRequestDto.ItemRequestCreateDto requestCreateDto = new ItemRequestDto.ItemRequestCreateDto("Need item");
        ItemRequestDto createdRequest = new ItemRequestDto(1L, "Need item", null, null);

        when(shareItClient.post(eq("/requests"),
                any(ItemRequestDto.ItemRequestCreateDto.class),
                eq(ItemRequestDto.class),
                eq(1L)))
                .thenReturn(Mono.just(createdRequest));

        webTestClient.post()
                .uri("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestCreateDto)
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.description").isEqualTo("Need item");
    }

    @Test
    void getUserRequests_shouldReturnList() {
        ParameterizedTypeReference<List<ItemRequestDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(eq("/requests"), eq(typeRef), eq(1L)))
                .thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/requests")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllRequests_shouldReturnList() {
        ParameterizedTypeReference<List<ItemRequestDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(eq("/requests/all?from=0&size=10"), eq(typeRef), eq(1L)))
                .thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/requests/all?from=0&size=10")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        ItemRequestDto request = new ItemRequestDto(1L, "Need item", null, null);

        when(shareItClient.get(eq("/requests/1"), eq(ItemRequestDto.class), eq(1L)))
                .thenReturn(Mono.just(request));

        webTestClient.get()
                .uri("/requests/1")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getUserRequests_whenNoRequests_shouldReturnEmptyList() {
        ParameterizedTypeReference<List<ItemRequestDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get("/requests", typeRef, 1L))
                .thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/requests")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectBodyList(ItemRequestDto.class)
                .hasSize(0);
    }

}