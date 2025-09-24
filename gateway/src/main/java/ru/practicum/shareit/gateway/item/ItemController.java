package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.util.GatewayConstants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ShareItClient shareItClient;

    @PostMapping
    public Mono<ResponseEntity<ItemDto>> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(GatewayConstants.USER_ID_HEADER) Long ownerId) {
        log.info("Creating item for owner {}: {}", ownerId, itemDto);
        return shareItClient.post("/items", itemDto, ItemDto.class, ownerId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error creating item: {}", error.getMessage()));
    }

    @PatchMapping("/{itemId}")
    public Mono<ResponseEntity<ItemDto>> updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(GatewayConstants.USER_ID_HEADER) Long ownerId) {
        log.info("Updating item {} for owner {}: {}", itemId, ownerId, itemDto);
        return shareItClient.patch("/items/" + itemId, itemDto, ItemDto.class, ownerId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error updating item: {}", error.getMessage()));
    }

    @GetMapping("/{itemId}")
    public Mono<ResponseEntity<ItemDto>> getItemById(
            @PathVariable Long itemId,
            @RequestHeader(GatewayConstants.USER_ID_HEADER) Long userId) {
        log.info("Getting item {} for user {}", itemId, userId);
        return shareItClient.get("/items/" + itemId, ItemDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting item: {}", error.getMessage()));
    }

    @GetMapping
    public Mono<ResponseEntity<List<ItemDto>>> getAllItemsByOwner(
            @RequestHeader(GatewayConstants.USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all items for owner {} from {} size {}", ownerId, from, size);
        String path = String.format("/items?from=%d&size=%d", from, size);
        ParameterizedTypeReference<List<ItemDto>> typeReference =
                new ParameterizedTypeReference<List<ItemDto>>() {
                };
        return shareItClient.get(path, typeReference, ownerId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting owner items: {}", error.getMessage()));
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<ItemDto>>> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = GatewayConstants.USER_ID_HEADER, required = false) Long userId) {
        log.info("Searching items with text: '{}'", text);
        String path = String.format("/items/search?text=%s&from=%d&size=%d", text, from, size);
        ParameterizedTypeReference<List<ItemDto>> typeReference =
                new ParameterizedTypeReference<List<ItemDto>>() {
                };
        return shareItClient.get(path, typeReference, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error searching items: {}", error.getMessage()));
    }

    @PostMapping("/{itemId}/comment")
    public Mono<ResponseEntity<CommentDto>> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader(GatewayConstants.USER_ID_HEADER) Long userId) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return shareItClient.post("/items/" + itemId + "/comment", commentDto, CommentDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error adding comment: {}", error.getMessage()));
    }
}