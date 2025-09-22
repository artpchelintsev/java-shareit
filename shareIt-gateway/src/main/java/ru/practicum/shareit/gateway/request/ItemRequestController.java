package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.client.ShareItClient;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ShareItClient shareItClient;

    @PostMapping
    public Mono<ResponseEntity<ItemRequestDto>> createItemRequest(
            @Valid @RequestBody ItemRequestDto.ItemRequestCreateDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item request for user {}: {}", userId, itemRequestDto);
        return shareItClient.post("/requests", itemRequestDto, ItemRequestDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error creating item request: {}", error.getMessage()));
    }

    @GetMapping
    public Mono<ResponseEntity<List<ItemRequestDto>>> getUserItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item requests for user {}", userId);
        ParameterizedTypeReference<List<ItemRequestDto>> typeReference =
                new ParameterizedTypeReference<List<ItemRequestDto>>() {};
        return shareItClient.get("/requests", typeReference, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting user item requests: {}", error.getMessage()));
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<List<ItemRequestDto>>> getOtherUsersItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting other users item requests for user {} from {} size {}", userId, from, size);
        String path = String.format("/requests/all?from=%d&size=%d", from, size);
        ParameterizedTypeReference<List<ItemRequestDto>> typeReference =
                new ParameterizedTypeReference<List<ItemRequestDto>>() {};
        return shareItClient.get(path, typeReference, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting other users item requests: {}", error.getMessage()));
    }

    @GetMapping("/{requestId}")
    public Mono<ResponseEntity<ItemRequestDto>> getItemRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item request {} for user {}", requestId, userId);
        return shareItClient.get("/requests/" + requestId, ItemRequestDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting item request: {}", error.getMessage()));
    }
}