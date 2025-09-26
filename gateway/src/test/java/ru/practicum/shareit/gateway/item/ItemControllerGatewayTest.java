package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.util.GatewayConstants;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
class ItemControllerGatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShareItClient shareItClient;

    @Test
    void createItem_shouldReturnItem() {
        ItemDto itemRequest = new ItemDto(null, "Item1", "Desc", true, null, null, null, null);
        ItemDto createdItem = new ItemDto(1L, "Item1", "Desc", true, null, null, null, null);

        when(shareItClient.post(eq("/items"), any(ItemDto.class), eq(ItemDto.class), eq(1L)))
                .thenReturn(Mono.just(createdItem));

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(itemRequest)
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Item1")
                .jsonPath("$.description").isEqualTo("Desc");

        verify(shareItClient, times(1)).post(eq("/items"), any(ItemDto.class), eq(ItemDto.class), eq(1L));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() {
        ItemDto updateRequest = new ItemDto(null, "ItemUpdated", "DescUpdated", true, null, null, null, null);
        ItemDto updatedItem = new ItemDto(1L, "ItemUpdated", "DescUpdated", true, null, null, null, null);

        when(shareItClient.patch(eq("/items/1"), any(ItemDto.class), eq(ItemDto.class), eq(1L)))
                .thenReturn(Mono.just(updatedItem));

        webTestClient.patch()
                .uri("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("ItemUpdated")
                .jsonPath("$.description").isEqualTo("DescUpdated");

        verify(shareItClient, times(1)).patch(eq("/items/1"), any(ItemDto.class), eq(ItemDto.class), eq(1L));
    }

    @Test
    void getItemById_shouldReturnItem() {
        ItemDto item = new ItemDto(1L, "Item1", "Desc", true, null, null, null, null);

        when(shareItClient.get(eq("/items/1"), eq(ItemDto.class), eq(1L)))
                .thenReturn(Mono.just(item));

        webTestClient.get()
                .uri("/items/1")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Item1");

        verify(shareItClient, times(1)).get(eq("/items/1"), eq(ItemDto.class), eq(1L));
    }

    @Test
    void getAllItemsByOwner_shouldReturnList() {
        ItemDto item1 = new ItemDto(1L, "Item1", "Desc1", true, null, null, null, null);
        ItemDto item2 = new ItemDto(2L, "Item2", "Desc2", true, null, null, null, null);
        List<ItemDto> items = List.of(item1, item2);

        ParameterizedTypeReference<List<ItemDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(eq("/items?from=0&size=10"), eq(typeRef), eq(1L)))
                .thenReturn(Mono.just(items));

        webTestClient.get()
                .uri("/items?from=0&size=10")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemDto.class)
                .hasSize(2)
                .contains(item1, item2);

        verify(shareItClient, times(1)).get(eq("/items?from=0&size=10"), eq(typeRef), eq(1L));
    }

    @Test
    void searchItems_shouldReturnList() {
        ItemDto item = new ItemDto(1L, "SearchItem", "Desc", true, null, null, null, null);
        List<ItemDto> items = Collections.singletonList(item);

        ParameterizedTypeReference<List<ItemDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(eq("/items/search?text=Search&from=0&size=10"), eq(typeRef), eq(1L)))
                .thenReturn(Mono.just(items));

        webTestClient.get()
                .uri("/items/search?text=Search&from=0&size=10")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemDto.class)
                .hasSize(1)
                .contains(item);

        verify(shareItClient, times(1)).get(eq("/items/search?text=Search&from=0&size=10"), eq(typeRef), eq(1L));
    }

    @Test
    void addComment_shouldReturnComment() {
        CommentDto commentRequest = new CommentDto(null, "Nice item", "User1", null);
        CommentDto createdComment = new CommentDto(1L, "Nice item", "User1", null);

        when(shareItClient.post(eq("/items/1/comment"), any(CommentDto.class), eq(CommentDto.class), eq(1L)))
                .thenReturn(Mono.just(createdComment));

        webTestClient.post()
                .uri("/items/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentRequest)
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.text").isEqualTo("Nice item");

        verify(shareItClient, times(1)).post(eq("/items/1/comment"), any(CommentDto.class), eq(CommentDto.class), eq(1L));
    }

    @Test
    void createItem_withMissingUserId_shouldReturnError() {
        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ItemDto(null, "Item", "Desc", true, null, null, null, null))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getItemById_whenItemNotFound_shouldReturnEmptyBody() {
        when(shareItClient.get("/items/99", ItemDto.class, 1L))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/items/99")
                .header(GatewayConstants.USER_ID_HEADER, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

}
