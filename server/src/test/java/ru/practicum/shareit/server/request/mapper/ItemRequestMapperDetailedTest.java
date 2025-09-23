package ru.practicum.shareit.server.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperDetailedTest {

    @Test
    void toItemRequestDto_shouldHandleNullItems() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(null); // Items are null

        // When
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need item", dto.getDescription());
        assertNull(dto.getItems()); // Items should be null
    }

    @Test
    void toItemRequestDto_shouldHandleEmptyItems() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of()); // Empty items list

        // When
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need item", dto.getDescription());
        assertTrue(dto.getItems().isEmpty()); // Items should be empty
    }

    @Test
    void toItemRequestDto_shouldMapItemsCorrectly() {
        // Given
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of(item));

        // When
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need item", dto.getDescription());
        assertFalse(dto.getItems().isEmpty());
        assertEquals(1L, dto.getItems().get(0).getId());
        assertEquals("Test Item", dto.getItems().get(0).getName());
    }

    @Test
    void toItemRequest_shouldCreateRequestFromCreateDto() {
        // Given
        ItemRequestDto.ItemRequestCreateDto createDto =
                new ItemRequestDto.ItemRequestCreateDto("Need item");

        // When
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(createDto);

        // Then
        assertNotNull(itemRequest);
        assertEquals("Need item", itemRequest.getDescription());
    }


}