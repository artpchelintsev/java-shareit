package ru.practicum.shareit.server.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto.ItemRequestCreateDto itemRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}