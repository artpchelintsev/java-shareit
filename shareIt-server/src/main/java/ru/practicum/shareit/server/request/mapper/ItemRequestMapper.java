package ru.practicum.shareit.server.request.mapper;

import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            dto.setItems(itemRequest.getItems().stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto.ItemRequestCreateDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        return itemRequest;
    }
}