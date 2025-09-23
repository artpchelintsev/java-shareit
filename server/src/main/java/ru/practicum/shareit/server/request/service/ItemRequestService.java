package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto.ItemRequestCreateDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getOtherUsersItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}